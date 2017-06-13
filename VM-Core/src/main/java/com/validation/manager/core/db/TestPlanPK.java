/* 
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
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
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Embeddable
public class TestPlanPK implements Serializable {

    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Test_Plan_IDGEN")
    @TableGenerator(name = "Test_Plan_IDGEN",
            table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "test_plan",
            initialValue = 1_000,
            allocationSize = 1)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_project_id")
    private int testProjectId;

    public TestPlanPK() {
    }

    public TestPlanPK(int testProjectId) {
        this.testProjectId = testProjectId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTestProjectId() {
        return testProjectId;
    }

    public void setTestProjectId(int testProjectId) {
        this.testProjectId = testProjectId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) testProjectId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof TestPlanPK)) {
            return false;
        }
        TestPlanPK other = (TestPlanPK) object;
        if (this.id != other.id) {
            return false;
        }
        return this.testProjectId == other.testProjectId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestPlanPK[ id=" + id
                + ", testProjectId=" + testProjectId + " ]";
    }
}
