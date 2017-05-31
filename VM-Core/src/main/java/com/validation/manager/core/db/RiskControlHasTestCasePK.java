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
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Embeddable
public class RiskControlHasTestCasePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "risk_control_id")
    private int riskControlId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "risk_control_risk_control_type_id")
    private int riskControlRiskControlTypeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_case_id")
    private int testCaseId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "test_case_test_id")
    private int testCaseTestId;

    public RiskControlHasTestCasePK() {
    }

    public RiskControlHasTestCasePK(int riskControlId,
            int riskControlRiskControlTypeId, int testCaseId,
            int testCaseTestId) {
        this.riskControlId = riskControlId;
        this.riskControlRiskControlTypeId = riskControlRiskControlTypeId;
        this.testCaseId = testCaseId;
        this.testCaseTestId = testCaseTestId;
    }

    public int getRiskControlId() {
        return riskControlId;
    }

    public void setRiskControlId(int riskControlId) {
        this.riskControlId = riskControlId;
    }

    public int getRiskControlRiskControlTypeId() {
        return riskControlRiskControlTypeId;
    }

    public void setRiskControlRiskControlTypeId(int riskControlRiskControlTypeId) {
        this.riskControlRiskControlTypeId = riskControlRiskControlTypeId;
    }

    public int getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(int testCaseId) {
        this.testCaseId = testCaseId;
    }

    public int getTestCaseTestId() {
        return testCaseTestId;
    }

    public void setTestCaseTestId(int testCaseTestId) {
        this.testCaseTestId = testCaseTestId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) riskControlId;
        hash += (int) riskControlRiskControlTypeId;
        hash += (int) testCaseId;
        hash += (int) testCaseTestId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof RiskControlHasTestCasePK)) {
            return false;
        }
        RiskControlHasTestCasePK other = (RiskControlHasTestCasePK) object;
        if (this.riskControlId != other.riskControlId) {
            return false;
        }
        if (this.riskControlRiskControlTypeId != other.riskControlRiskControlTypeId) {
            return false;
        }
        if (this.testCaseId != other.testCaseId) {
            return false;
        }
        return this.testCaseTestId == other.testCaseTestId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskControlHasTestCasePK[ "
                + "riskControlId=" + riskControlId
                + ", riskControlRiskControlTypeId="
                + riskControlRiskControlTypeId + ", testCaseId="
                + testCaseId + ", testCaseTestId=" + testCaseTestId + " ]";
    }
}
