/*
 * Copyright 2018 Daan Wendelen
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
package com.github.dwendelen.testing.component.impl.component;

public class ActualDependency {
    private Component from;
    private AbstractCodeContainer to;
    private String className;

    public ActualDependency(Component from, AbstractCodeContainer to, String className) {
        this.from = from;
        this.to = to;
        this.className = className;
    }

    public Component getFrom() {
        return from;
    }

    public AbstractCodeContainer getTo() {
        return to;
    }

    public String getClassName() {
        return className;
    }
}
