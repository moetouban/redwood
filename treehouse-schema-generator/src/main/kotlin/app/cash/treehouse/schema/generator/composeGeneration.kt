/*
 * Copyright (C) 2021 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.treehouse.schema.generator

import app.cash.exhaustive.Exhaustive
import app.cash.treehouse.schema.parser.Children
import app.cash.treehouse.schema.parser.Event
import app.cash.treehouse.schema.parser.Property
import app.cash.treehouse.schema.parser.Schema
import app.cash.treehouse.schema.parser.Widget
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.KModifier.PUBLIC
import com.squareup.kotlinpoet.NOTHING
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.joinToCode
import app.cash.treehouse.schema.generator.widget as widgetType

/*
@Composable
fun Button(
  text: String,
  enabled: Boolean = true,
  onClick: (() -> Unit)? = null,
) {
  ComposeNode<Button<*>, Applier<Widget<*>>>(
    factory = {
      factory.SunspotButton()
    },
    update = {
      set(text) { text(text) }
      set(enabled) { enabled(enabled) }
      set(onClick) { onClick(onClick) }
    },
  )
}
*/
internal fun generateComposeNode(schema: Schema, widget: Widget): FileSpec {
  val applierOfServerNode = applier.parameterizedBy(protocolNode)
  return FileSpec.builder(schema.composePackage, widget.flatName)
    .addFunction(
      FunSpec.builder(widget.flatName)
        .addModifiers(PUBLIC)
        .addAnnotation(composable)
        .receiver(treehouseScope) // TODO generate this?
        .apply {
          for (trait in widget.traits) {
            addParameter(
              when (trait) {
                is Property -> {
                  ParameterSpec.builder(trait.name, trait.type.asTypeName())
                    .apply {
                      trait.defaultExpression?.let { defaultValue(it) }
                    }
                    .build()
                }
                is Event -> {
                  ParameterSpec.builder(trait.name, eventLambda.copy(nullable = true))
                    .defaultValue("null")
                    .build()
                }
                is Children -> {
                  ParameterSpec.builder(trait.name, composableLambda)
                    .build()
                }
              }
            )
          }

          val arguments = mutableListOf<CodeBlock>()

          arguments += CodeBlock.builder()
            .add("factory = {\n")
            .indent()
            .add("factory.%N()\n", widget.flatName)
            .unindent()
            .add("}")
            .build()

          val updateLambda = CodeBlock.builder()
          val childrenLambda = CodeBlock.builder()
          for (trait in widget.traits) {
            @Exhaustive when (trait) {
              is Property, is Event -> {
                updateLambda.apply {
                  add("set(%1N) { %1N(%1N) }\n", trait.name)
                }
              }
              is Children -> {
                childrenLambda.apply {
                  add("%M(%L) {\n", syntheticChildren, trait.tag)
                  indent()
                  add("%N()\n", trait.name)
                  unindent()
                  add("}\n")
                }
              }
            }
          }

          arguments += CodeBlock.builder()
            .add("update = {\n")
            .indent()
            .add(updateLambda.build())
            .unindent()
            .add("}")
            .build()

          if (childrenLambda.isNotEmpty()) {
            arguments += CodeBlock.builder()
              .add("content = {\n")
              .indent()
              .add(childrenLambda.build())
              .unindent()
              .add("}")
              .build()
          }

          val nodeType = if (widget.traits.any { it is Event }) {
            widgetType
          } else {
            schema.widgetType(widget)
          }
          addStatement(
            "%M<%T, %T>(%L)", composeNode, nodeType.parameterizedBy(STAR), applierOfServerNode,
            arguments.joinToCode(",\n", "\n", ",\n")
          )
        }
        .build()
    )
    .build()
}

/*
class ProtocolSunspotWidgetFactory(
  private val scope: TreehouseScope,
) : SunspotWidgetFactory<Nothing> {
  override fun SunspotBox(): SunspotBox<Nothing> = ProtocolSunspotBox(scope)
  // ...
}

private class ProtocolSunspotBox(
  private val scope: TreehouseScope,
) : ProtocolNode(scope.nextId(), 1), SunspotBox<Nothing> {
  // ...
}
*/
internal fun generateProtocolFactory(schema: Schema): FileSpec {
  return FileSpec.builder(schema.composePackage, "protocolFactory")
    .addType(TypeSpec.classBuilder("Protocol${schema.name}WidgetFactory")
      .addSuperinterface(schema.getWidgetFactoryType().parameterizedBy(NOTHING))
      .primaryConstructor(FunSpec.constructorBuilder()
        .addParameter("scope", treehouseScope)
        .build())
      .addProperty(PropertySpec.builder("scope", treehouseScope, PRIVATE)
        .initializer("scope")
        .build())
      .apply {
        for (widget in schema.widgets) {
          addFunction(FunSpec.builder(widget.flatName)
            .addModifiers(OVERRIDE)
            .returns(schema.widgetType(widget).parameterizedBy(NOTHING))
            .addStatement("return %T(scope)", schema.protocolType(widget))
            .build())
        }
      }
      .build())
    .apply {
      for (widget in schema.widgets) {
        addType(TypeSpec.classBuilder(schema.protocolType(widget))
          .addModifiers(PRIVATE)
          .primaryConstructor(FunSpec.constructorBuilder()
            .addParameter("scope", treehouseScope)
            .build())
          .addProperty(PropertySpec.builder("scope", treehouseScope, PRIVATE)
            .initializer("scope")
            .build())
          .superclass(protocolNode)
          .addSuperclassConstructorParameter("scope.nextId()")
          .addSuperclassConstructorParameter("%L", widget.tag)
          .addSuperinterface(schema.widgetType(widget).parameterizedBy(NOTHING))
          .addProperty(PropertySpec.builder("value", NOTHING, OVERRIDE)
            .getter(FunSpec.getterBuilder()
              .addStatement("throw %T()", uoe)
              .build())
            .build())
          .apply {
            for (trait in widget.traits) {
              @Exhaustive when (trait) {
                is Property -> {
                  addFunction(
                    FunSpec.builder(trait.name)
                      .addModifiers(PUBLIC, OVERRIDE)
                      .addParameter(trait.name, trait.type.asTypeName())
                      .addStatement("scope.appendDiff(%T(this.id, %L, %N))", propertyDiff, trait.tag, trait.name)
                      .build()
                  )
                }
                is Event -> {
                  addProperty(
                    PropertySpec.builder(trait.name, eventLambda.copy(nullable = true), PRIVATE)
                      .mutable(true)
                      .initializer("null")
                      .build()
                  )
                  addFunction(
                    FunSpec.builder(trait.name)
                      .addModifiers(PUBLIC, OVERRIDE)
                      .addParameter(trait.name, eventLambda)
                      .addStatement("val %1NSet = %1N != null", trait.name)
                      .beginControlFlow("if (%1NSet != (this.%1N != null))", trait.name)
                      .addStatement("scope.appendDiff(%T(this.id, %L, %NSet))", propertyDiff, trait.tag, trait.name)
                      .endControlFlow()
                      .addStatement("this.%1N = %1N", trait.name)
                      .build()
                  )
                }
                is Children -> {
                  addProperty(
                    PropertySpec.builder(trait.name, widgetChildren.parameterizedBy(NOTHING))
                      .addModifiers(PUBLIC, OVERRIDE)
                      .getter(FunSpec.getterBuilder()
                        .addStatement("throw %T()", uoe)
                        .build())
                      .build()
                  )
                }
              }
            }

            val events = widget.traits.filterIsInstance<Event>()
            if (events.isNotEmpty()) {
              addFunction(
                FunSpec.builder("sendEvent")
                  .addModifiers(OVERRIDE)
                  .addParameter("event", eventType)
                  .beginControlFlow("when (val tag = event.tag)")
                  .apply {
                    for (event in events.sortedBy { it.tag }) {
                      addStatement("%L -> %N?.invoke()", event.tag, event.name)
                    }
                  }
                  .addStatement("else -> throw %T(\"Unknown tag \$tag\")", iae)
                  .endControlFlow()
                  .build()
              )
            }
          }
          .build())
      }
    }
    .build()
}
