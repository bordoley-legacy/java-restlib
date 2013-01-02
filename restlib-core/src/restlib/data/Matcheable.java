/*
 * Copyright (C) 2012 David Bordoley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package restlib.data;

public interface Matcheable<T> {
    /**
     * Defines the quality of the match of this to that. For instance,
     * for a MediaRange this text/* would match any that text/ 
     * MediaRange, however text/html would not match text/*
     * @param that
     * @return The quality of the match. 0 is no match, 1000 is an exact match.
     * Values in between are defined by the implementation.
     */
    public int match(T that);
}
