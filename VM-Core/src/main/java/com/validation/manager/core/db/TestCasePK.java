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
public class TestCasePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "TestCase_IDGEN")
    @TableGenerator(name = "TestCase_IDGEN", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "test_case",
            initialValue = 1_000,
            allocationSize = 1)
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_case_type_id")
    private int testCaseTypeId;

    public TestCasePK() {
    }

    public TestCasePK(int testCaseTypeId) {
        this.testCaseTypeId = testCaseTypeId;
    }

    public TestCasePK(int testCaseId, int testCaseTypeId) {
        this.testCaseTypeId = testCaseTypeId;
        this.id = testCaseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTestCaseTypeId() {
        return testCaseTypeId;
    }

    public void setTestCaseTypeId(int testCaseTypeId) {
        this.testCaseTypeId = testCaseTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) testCaseTypeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TestCasePK)) {
            return false;
        }
        TestCasePK other = (TestCasePK) object;
        if (this.id != other.id) {
            return false;
        }
        return this.testCaseTypeId == other.testCaseTypeId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.TestCasePK[ id=" + id
                + ", testCaseTypeId=" + testCaseTypeId + " ]";
    }
}
