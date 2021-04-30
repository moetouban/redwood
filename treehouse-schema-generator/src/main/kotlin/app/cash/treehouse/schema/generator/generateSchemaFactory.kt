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
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
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

private class SchemaSunspotBox : SunspotBox<Any> {
  // ...
}
*/
internal fun generateSchemaFactory(schema: Schema): FileSpec {
  return FileSpec.builder(schema.composePackage, "schemaFactory")
    .addType(TypeSpec.classBuilder("Schema${schema.name}WidgetFactory")
      .addSuperinterface(schema.getWidgetFactoryType().parameterizedBy(ANY))
      .apply {
        for (widget in schema.widgets) {
          addFunction(FunSpec.builder(widget.flatName)
            .addModifiers(KModifier.OVERRIDE)
            .returns(schema.widgetType(widget).parameterizedBy(ANY))
            .addStatement("return %T()", schema.schemaType(widget))
            .build())
        }
      }
      .build())
    .apply {
      for (widget in schema.widgets) {
        addType(TypeSpec.classBuilder(schema.schemaType(widget))
          .addModifiers(KModifier.PRIVATE)
          .addSuperinterface(schema.widgetType(widget).parameterizedBy(ANY))
          .apply {
            val schemaParameters = mutableListOf<CodeBlock>()
            for (trait in widget.traits) {
              @Exhaustive when (trait) {
                is Property -> {
                  addProperty(
                    PropertySpec.builder(trait.name, trait.type.asTypeName().copy(nullable = true),
                      KModifier.PRIVATE)
                      .mutable(true)
                      .initializer("null")
                      .build()
                  )
                  addFunction(
                    FunSpec.builder(trait.name)
                      .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                      .addParameter(trait.name, trait.type.asTypeName())
                      .addStatement("this.%1N = %1N", trait.name)
                      .build()
                  )
                  schemaParameters += CodeBlock.of(
                    if (trait.type.isMarkedNullable) "%N" else "%N!!", trait.name)
                }
                is Event -> {
                  addProperty(
                    PropertySpec.builder(trait.name, eventLambda.copy(nullable = true),
                      KModifier.PRIVATE)
                      .mutable(true)
                      .initializer("null")
                      .build()
                  )
                  addFunction(
                    FunSpec.builder(trait.name)
                      .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                      .addParameter(trait.name, eventLambda.copy(nullable = true))
                      .addStatement("this.%1N = %1N", trait.name)
                      .build()
                  )
                  schemaParameters += CodeBlock.of("%N ?: {}", trait.name) // TODO throw?
                }
                is Children -> {
                  addProperty(
                    PropertySpec.builder(trait.name, mutableListChildren.parameterizedBy(ANY))
                      .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                      .initializer("%T(mutableListOf())", mutableListChildren)
                      .build()
                  )
                  schemaParameters += CodeBlock.of("%N.list.toList()", trait.name)
                }
              }
            }
            addProperty(PropertySpec.builder("value", ANY, KModifier.OVERRIDE)
              .getter(FunSpec.getterBuilder()
                .addStatement("return %T(%L)", widget.type.asClassName(), schemaParameters.joinToCode())
                .build())
              .build())
          }
          .build())
      }
    }
    .build()
}
