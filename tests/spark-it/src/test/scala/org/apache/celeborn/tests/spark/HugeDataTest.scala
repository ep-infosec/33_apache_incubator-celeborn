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

package org.apache.celeborn.tests.spark

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.funsuite.AnyFunSuite

import org.apache.celeborn.client.ShuffleClient

class HugeDataTest extends AnyFunSuite
  with SparkTestBase
  with BeforeAndAfterAll
  with BeforeAndAfterEach {

  override def beforeAll(): Unit = {
    logInfo("test initialized , setup rss mini cluster")
    tuple = setupRssMiniClusterSpark()
  }

  override def afterAll(): Unit = {
    logInfo("all test complete , stop rss mini cluster")
    clearMiniCluster(tuple)
  }

  override def beforeEach(): Unit = {
    ShuffleClient.reset()
  }

  override def afterEach(): Unit = {
    System.gc()
  }

  test("celeborn spark integration test - huge data") {
    val sparkConf = new SparkConf().setAppName("rss-demo").setMaster("local[4]")
    val ss = SparkSession.builder().config(updateSparkConf(sparkConf, false)).getOrCreate()
    val value = Range(1, 10000).mkString(",")
    val tuples = ss.sparkContext.parallelize(1 to 10000, 2)
      .map { i => (i, value) }.groupByKey(16).collect()

    // verify result
    assert(tuples.length == 10000)
    for (elem <- tuples) {
      assert(elem._2.mkString(",").equals(value))
    }

    ss.stop()
  }
}
