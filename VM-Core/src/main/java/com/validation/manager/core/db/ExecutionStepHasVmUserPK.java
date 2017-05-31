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
public class ExecutionStepHasVmUserPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "execution_step_test_case_execution_id")
    private int executionStepTestCaseExecutionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "execution_step_step_id")
    private int executionStepStepId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "execution_step_step_test_case_id")
    private int executionStepStepTestCaseId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "vm_user_id")
    private int vmUserId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "vm_user_execution_step_test_case_execution_id")
    private int vmUserExecutionStepTestCaseExecutionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "vm_user_execution_step_step_id")
    private int vmUserExecutionStepStepId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "vm_user_execution_step_step_test_case_id")
    private int vmUserExecutionStepStepTestCaseId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "role_id")
    private int roleId;

    public ExecutionStepHasVmUserPK() {
    }

    public ExecutionStepHasVmUserPK(int executionStepTestCaseExecutionId,
            int executionStepStepId, int executionStepStepTestCaseId,
            int vmUserId, int vmUserExecutionStepTestCaseExecutionId,
            int vmUserExecutionStepStepId,
            int vmUserExecutionStepStepTestCaseId, int roleId) {
        this.executionStepTestCaseExecutionId = executionStepTestCaseExecutionId;
        this.executionStepStepId = executionStepStepId;
        this.executionStepStepTestCaseId = executionStepStepTestCaseId;
        this.vmUserId = vmUserId;
        this.vmUserExecutionStepTestCaseExecutionId = vmUserExecutionStepTestCaseExecutionId;
        this.vmUserExecutionStepStepId = vmUserExecutionStepStepId;
        this.vmUserExecutionStepStepTestCaseId = vmUserExecutionStepStepTestCaseId;
        this.roleId = roleId;
    }

    public int getExecutionStepTestCaseExecutionId() {
        return executionStepTestCaseExecutionId;
    }

    public void setExecutionStepTestCaseExecutionId(int executionStepTestCaseExecutionId) {
        this.executionStepTestCaseExecutionId = executionStepTestCaseExecutionId;
    }

    public int getExecutionStepStepId() {
        return executionStepStepId;
    }

    public void setExecutionStepStepId(int executionStepStepId) {
        this.executionStepStepId = executionStepStepId;
    }

    public int getExecutionStepStepTestCaseId() {
        return executionStepStepTestCaseId;
    }

    public void setExecutionStepStepTestCaseId(int executionStepStepTestCaseId) {
        this.executionStepStepTestCaseId = executionStepStepTestCaseId;
    }

    public int getVmUserId() {
        return vmUserId;
    }

    public void setVmUserId(int vmUserId) {
        this.vmUserId = vmUserId;
    }

    public int getVmUserExecutionStepTestCaseExecutionId() {
        return vmUserExecutionStepTestCaseExecutionId;
    }

    public void setVmUserExecutionStepTestCaseExecutionId(int vmUserExecutionStepTestCaseExecutionId) {
        this.vmUserExecutionStepTestCaseExecutionId = vmUserExecutionStepTestCaseExecutionId;
    }

    public int getVmUserExecutionStepStepId() {
        return vmUserExecutionStepStepId;
    }

    public void setVmUserExecutionStepStepId(int vmUserExecutionStepStepId) {
        this.vmUserExecutionStepStepId = vmUserExecutionStepStepId;
    }

    public int getVmUserExecutionStepStepTestCaseId() {
        return vmUserExecutionStepStepTestCaseId;
    }

    public void setVmUserExecutionStepStepTestCaseId(int vmUserExecutionStepStepTestCaseId) {
        this.vmUserExecutionStepStepTestCaseId = vmUserExecutionStepStepTestCaseId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) executionStepTestCaseExecutionId;
        hash += (int) executionStepStepId;
        hash += (int) executionStepStepTestCaseId;
        hash += (int) vmUserId;
        hash += (int) vmUserExecutionStepTestCaseExecutionId;
        hash += (int) vmUserExecutionStepStepId;
        hash += (int) vmUserExecutionStepStepTestCaseId;
        hash += (int) roleId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ExecutionStepHasVmUserPK)) {
            return false;
        }
        ExecutionStepHasVmUserPK other = (ExecutionStepHasVmUserPK) object;
        if (this.executionStepTestCaseExecutionId != other.executionStepTestCaseExecutionId) {
            return false;
        }
        if (this.executionStepStepId != other.executionStepStepId) {
            return false;
        }
        if (this.executionStepStepTestCaseId != other.executionStepStepTestCaseId) {
            return false;
        }
        if (this.vmUserId != other.vmUserId) {
            return false;
        }
        if (this.vmUserExecutionStepTestCaseExecutionId != other.vmUserExecutionStepTestCaseExecutionId) {
            return false;
        }
        if (this.vmUserExecutionStepStepId != other.vmUserExecutionStepStepId) {
            return false;
        }
        if (this.vmUserExecutionStepStepTestCaseId != other.vmUserExecutionStepStepTestCaseId) {
            return false;
        }
        return this.roleId == other.roleId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExecutionStepHasVmUserPK[ "
                + "executionStepTestCaseExecutionId="
                + executionStepTestCaseExecutionId + ", executionStepStepId="
                + executionStepStepId + ", executionStepStepTestCaseId="
                + executionStepStepTestCaseId + ", vmUserId=" + vmUserId
                + ", vmUserExecutionStepTestCaseExecutionId="
                + vmUserExecutionStepTestCaseExecutionId
                + ", vmUserExecutionStepStepId=" + vmUserExecutionStepStepId
                + ", vmUserExecutionStepStepTestCaseId="
                + vmUserExecutionStepStepTestCaseId + ", roleId=" + roleId + " ]";
    }
}
