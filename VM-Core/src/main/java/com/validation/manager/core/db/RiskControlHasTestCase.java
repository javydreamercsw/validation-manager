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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "risk_control_has_test_case")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RiskControlHasTestCase.findAll",
            query = "SELECT r FROM RiskControlHasTestCase r")
    , @NamedQuery(name = "RiskControlHasTestCase.findByRiskControlId",
            query = "SELECT r FROM RiskControlHasTestCase r WHERE "
            + "r.riskControlHasTestCasePK.riskControlId = :riskControlId")
    , @NamedQuery(name = "RiskControlHasTestCase.findByRiskControlRiskControlTypeId",
            query = "SELECT r FROM RiskControlHasTestCase r WHERE "
            + "r.riskControlHasTestCasePK.riskControlRiskControlTypeId "
            + "= :riskControlRiskControlTypeId")
    , @NamedQuery(name = "RiskControlHasTestCase.findByTestCaseId",
            query = "SELECT r FROM RiskControlHasTestCase r WHERE "
            + "r.riskControlHasTestCasePK.testCaseId = :testCaseId")
    , @NamedQuery(name = "RiskControlHasTestCase.findByTestCaseTestId",
            query = "SELECT r FROM RiskControlHasTestCase r WHERE "
            + "r.riskControlHasTestCasePK.testCaseTestId = :testCaseTestId")})
public class RiskControlHasTestCase implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RiskControlHasTestCasePK riskControlHasTestCasePK;
    @JoinColumns({
        @JoinColumn(name = "risk_control_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "risk_control_risk_control_type_id",
                referencedColumnName = "risk_control_type_id",
                insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private RiskControl riskControl;
    @JoinColumns({
        @JoinColumn(name = "test_case_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        ,@JoinColumn(name = "test_case_type_id", referencedColumnName = "test_case_type_id",
                insertable = false, updatable = false)
    })
    @ManyToOne(optional = false)
    private TestCase testCase;

    public RiskControlHasTestCase() {
    }

    public RiskControlHasTestCase(RiskControlHasTestCasePK riskControlHasTestCasePK) {
        this.riskControlHasTestCasePK = riskControlHasTestCasePK;
    }

    public RiskControlHasTestCase(int riskControlId,
            int riskControlRiskControlTypeId, int testCaseId,
            int testCaseTestId) {
        this.riskControlHasTestCasePK
                = new RiskControlHasTestCasePK(riskControlId,
                        riskControlRiskControlTypeId, testCaseId, testCaseTestId);
    }

    public RiskControlHasTestCasePK getRiskControlHasTestCasePK() {
        return riskControlHasTestCasePK;
    }

    public void setRiskControlHasTestCasePK(RiskControlHasTestCasePK riskControlHasTestCasePK) {
        this.riskControlHasTestCasePK = riskControlHasTestCasePK;
    }

    public RiskControl getRiskControl() {
        return riskControl;
    }

    public void setRiskControl(RiskControl riskControl) {
        this.riskControl = riskControl;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (riskControlHasTestCasePK != null ? riskControlHasTestCasePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof RiskControlHasTestCase)) {
            return false;
        }
        RiskControlHasTestCase other = (RiskControlHasTestCase) object;
        return !((this.riskControlHasTestCasePK == null
                && other.riskControlHasTestCasePK != null)
                || (this.riskControlHasTestCasePK != null
                && !this.riskControlHasTestCasePK.equals(other.riskControlHasTestCasePK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskControlHasTestCase[ "
                + "riskControlHasTestCasePK=" + riskControlHasTestCasePK + " ]";
    }
}
