/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.celeborn.plugin.flink.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.flink.util.ExecutorUtils;
import org.slf4j.Logger;

public class ThreadUtils {

  public static ThreadFactory createFactoryWithDefaultExceptionHandler(
      final String executorServiceName, final Logger LOG) {
    return new ThreadFactoryBuilder()
        .setNameFormat(executorServiceName + "-%d")
        .setDaemon(true)
        .setUncaughtExceptionHandler(
            (Thread t, Throwable e) ->
                LOG.error(
                    "exception in serviceName: {}, thread: {}",
                    executorServiceName,
                    t.getName(),
                    e))
        .build();
  }

  public static void shutdownExecutors(int timeoutSecs, ExecutorService executorService) {
    ExecutorUtils.gracefulShutdown(timeoutSecs, TimeUnit.SECONDS, executorService);
  }
}
