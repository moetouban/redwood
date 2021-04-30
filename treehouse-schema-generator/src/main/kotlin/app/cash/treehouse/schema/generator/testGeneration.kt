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
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.joinToCode

/*
class SchemaSunspotWidgetFactory : SunspotWidgetFactory<Nothing> {
  override fun SunspotBox(): SunspotBox<Nothing> = SchemaSunspotBox(scope)
  // ...
}
*/
internal fun generateSchemaWidgetFactory(schema: Schema): FileSpec {
  val className = ClassName(schema.testPackage, "Schema${schema.name}WidgetFactory")
  return FileSpec.builder(schema.testPackage, className.simpleName)
    .addType(
      TypeSpec.objectBuilder(className)
        .addSuperinterface(schema.getWidgetFactoryType().parameterizedBy(ANY))
        .apply {
          for (widget in schema.widgets) {
            addFunction(
              FunSpec.builder(widget.flatName)
                .addModifiers(OVERRIDE)
                .returns(schema.widgetType(widget).parameterizedBy(ANY))
                .addStatement("return %T()", schema.testType(widget))
                .build()
            )
          }
        }
        .build()
    )
    .build()
}

/*
private class SchemaSunspotBox : SunspotBox<Any> {
  // ...
}
 */
internal fun generateSchemaWidget(schema: Schema, widget: Widget): FileSpec {
  val className = schema.testType(widget)
  return FileSpec.builder(schema.testPackage, className.simpleName)
    .addType(
      TypeSpec.classBuilder(className)
        .addSuperinterface(schema.widgetType(widget).parameterizedBy(ANY))
        .addProperty(
          PropertySpec.builder("value", ANY, OVERRIDE)
            .getter(
              FunSpec.getterBuilder()
                .addStatement("return this")
                .build()
            )
            .build()
        )
        .apply {
          val schemaParameters = mutableListOf<CodeBlock>()
          for (trait in widget.traits) {
            @Exhaustive when (trait) {
              is Property -> {
                addProperty(
                  PropertySpec.builder(
                    trait.name, trait.type.asTypeName().copy(nullable = true),
                    PRIVATE
                  )
                    .mutable(true)
                    .initializer("null")
                    .build()
                )
                addFunction(
                  FunSpec.builder(trait.name)
                    .addModifiers(OVERRIDE)
                    .addParameter(trait.name, trait.type.asTypeName())
                    .addStatement("this.%1N = %1N", trait.name)
                    .build()
                )
                schemaParameters += if (trait.type.isMarkedNullable) {
                  CodeBlock.of("%1N = %1N", trait.name)
                } else {
                  CodeBlock.of("%1N = %1N ?: error(%2S)", trait.name, "Required property '${trait.name}' not set")
                }
              }
              is Event -> {
                addProperty(
                  PropertySpec.builder(
                    trait.name, eventLambda.copy(nullable = true),
                    PRIVATE
                  )
                    .mutable(true)
                    .initializer("null")
                    .build()
                )
                addFunction(
                  FunSpec.builder(trait.name)
                    .addModifiers(OVERRIDE)
                    .addParameter(trait.name, eventLambda.copy(nullable = true))
                    .addStatement("this.%1N = %1N", trait.name)
                    .build()
                )
                schemaParameters += CodeBlock.of(
                  "%1N = %1N ?: { error(%2S) }", trait.name, "No '${trait.name}' lambda set"
                )
              }
              is Children -> {
                addProperty(
                  PropertySpec.builder(trait.name, mutableListChildren.parameterizedBy(ANY))
                    .addModifiers(OVERRIDE)
                    .initializer("%T()", mutableListChildren)
                    .build()
                )
                schemaParameters += CodeBlock.of("%1N = %1N.list.toList()", trait.name)
              }
            }
          }
          addProperty(
            PropertySpec.builder("tree", ANY)
              .getter(
                FunSpec.getterBuilder()
                  .addStatement(
                    "return %T(%L)", widget.type.asClassName(),
                    schemaParameters.joinToCode(separator = ",\n", prefix = "\n", suffix = "\n")
                  )
                  .build()
              )
              .build()
          )
        }
        .build()
    )
    .build()
}
