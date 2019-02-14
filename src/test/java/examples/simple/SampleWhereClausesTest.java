/**
 *    Copyright 2016-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package examples.simple;

import static examples.simple.SimpleTableDynamicSqlSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import org.junit.jupiter.api.Test;
import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;

public class SampleWhereClausesTest {

    @Test
    public void simpleClause1() {
        SelectStatementProvider selectStatement = select(count())
                .from(simpleTable)
                .where(id, isEqualTo(3))
                .build()
                .render(RenderingStrategy.MYBATIS3);
        
        assertThat(selectStatement.getSelectStatement())
                .isEqualTo("select count(*) from SimpleTable where id = #{parameters.p1,jdbcType=INTEGER}");
    }
    
    @Test
    public void simpleClause2() {
        SelectStatementProvider selectStatement = select(count())
                .from(simpleTable, "a")
                .where()
                .and(id, isNull())
                .or(simpleTable.birthDate,isNotNull())
                .or(simpleTable.firstName,isInWhenPresent("a",null,"b"))
                .build()
                .render(RenderingStrategy.MYBATIS3);

        System.out.println("getSelectStatement = " + selectStatement.getSelectStatement());
        System.out.println("getParameters = " + selectStatement.getParameters());
//        assertThat(selectStatement.getSelectStatement()).isEqualTo("select count(*) from SimpleTable a where a.id is null");
    }
    
    @Test
    public void betweenClause() {
        SelectStatementProvider selectStatement = select(count())
                .from(simpleTable, "a")
                .where(id, isBetween(1).and(4))
                .build()
                .render(RenderingStrategy.MYBATIS3);
        
        assertThat(selectStatement.getSelectStatement())
            .isEqualTo("select count(*) from SimpleTable a where a.id between #{parameters.p1,jdbcType=INTEGER} and #{parameters.p2,jdbcType=INTEGER}");
    }

    @Test
    public void complexClause() {
        SelectStatementProvider selectStatement = select(count())
                .from(simpleTable, "a")
                .where(id, isGreaterThan(2))
                .or(occupation, isNull(), and(id, isLessThan(6)))
                .build()
                .render(RenderingStrategy.MYBATIS3);
        
        assertThat(selectStatement.getSelectStatement())
            .isEqualTo("select count(*) from SimpleTable a where a.id > #{parameters.p1,jdbcType=INTEGER} or (a.occupation is null and a.id < #{parameters.p2,jdbcType=INTEGER})");
    }
}
