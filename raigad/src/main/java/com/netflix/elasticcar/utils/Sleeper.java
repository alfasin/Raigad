/**
 * Copyright 2013 Netflix, Inc.
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
package com.netflix.elasticcar.utils;

import com.google.inject.ImplementedBy;

/**
 * An abstraction to {@link Thread#sleep(long)} so we can mock it in tests.
 */
@ImplementedBy(ThreadSleeper.class)
public interface Sleeper
{
    void sleep(long waitTimeMs) throws InterruptedException;
    void sleepQuietly(long waitTimeMs);
}