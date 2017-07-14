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
public class StepPK implements Serializable {

    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "StepGen")
    @TableGenerator(name = "StepGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "step",
            allocationSize = 1,
            initialValue = 1_000)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_case_id")
    private int testCaseId;

    public StepPK() {
    }

    public StepPK(int testCaseId) {
        this.testCaseId = testCaseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(int testCaseId) {
        this.testCaseId = testCaseId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) testCaseId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof StepPK)) {
            return false;
        }
        StepPK other = (StepPK) object;
        if (this.id != other.id) {
            return false;
        }
        return this.testCaseId == other.testCaseId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.StepPK[ id=" + id
                + ", testCaseId=" + testCaseId + " ]";
    }
}
