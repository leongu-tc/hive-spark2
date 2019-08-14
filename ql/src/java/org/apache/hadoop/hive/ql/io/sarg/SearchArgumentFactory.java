/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.ql.io.sarg;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.ColumnProjectionUtils;
import org.apache.hadoop.hive.ql.io.sarg.SearchArgument;
import org.apache.hadoop.hive.ql.io.sarg.SearchArgument.Builder;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.plan.ExprNodeGenericFuncDesc;
import org.apache.hadoop.hive.ql.plan.TableScanDesc;

/**
 * A factory for creating SearchArguments.
 */
public class SearchArgumentFactory {
  public static final String SARG_PUSHDOWN = "sarg.pushdown";

  public static SearchArgument create(ExprNodeGenericFuncDesc expression) {
    return new SearchArgumentImpl(expression);
  }

  public static Builder newBuilder() {
    return SearchArgumentImpl.newBuilder();
  }

  public static SearchArgument create(String kryo) {
    return SearchArgumentImpl.fromKryo(kryo);
  }

  public static SearchArgument createFromConf(Configuration conf) {
    String sargString = null;
    if ((sargString = conf.get(TableScanDesc.FILTER_EXPR_CONF_STR)) != null) {
      return create(Utilities.deserializeExpression(sargString));
    } else if ((sargString = conf.get(SARG_PUSHDOWN)) != null) {
      return create(sargString);
    }
    return null;
  }
}
