#!/bin/bash -e
#
# Copyright 2016-present The Material Motion Authors. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Usage: `install-local-dependency.sh <group> <name>`
#
#     Publishes the dependency <group>:<name> to the local maven repository
#     using `gradle install`. This builds the local changes in that project
#     and propagates them to dependent projects.
#
#     Used by local-dependency-substitution.gradle

group="$1"
name="$2"

dir="$(mdm dir $name)" || {
  cat << EOF
Failed to get the local repo path for dependency $group:$name.
Make sure you read through our Contributor essentials: https://material-motion.github.io/material-motion/team/essentials/

Especially make sure that:

* You have installed our team's mdm tool https://material-motion.github.io/material-motion/team/essentials/frequent_contributors/tools
    \$(mdm dir) should output the correct directory
* You have cloned the repo for $group:$name
    \$(mdm dir $name) should output the correct directory
EOF
  exit 1
}

cd "$dir"
./gradlew install || {
  echo "Failed to publish dependency $group:$name to the local maven repository."
  exit 1
}
