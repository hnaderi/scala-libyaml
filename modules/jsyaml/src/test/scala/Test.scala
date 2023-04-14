/*
 * Copyright 2023 Hossein Naderi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tests

import dev.hnaderi.libyaml._

class Test extends ParserTestSuite(JSYaml, JSYaml) {
  test("sanity") {
    val input = """
data:
  double-quoted: "data"
  single-quoted: 'data'
  not quoted: data
  folded: |
    line 1
    line 2
  quoted: >
    data 1
    data 2
    data 3
  boolean 1: true
  boolean 2: Yes
  boolean 3: false
  boolean 4: NO
  not boolean 1: "Yes"
  not boolean 2: "true"
---
{a: 1}
---
{b: 2}
"""
    val obj = JSYaml.parseDocuments[YAML](input)
    obj.foreach { y =>
      println(y)

      println()

      y.map(JSYaml.print).foreach(println)
    }
  }
}
