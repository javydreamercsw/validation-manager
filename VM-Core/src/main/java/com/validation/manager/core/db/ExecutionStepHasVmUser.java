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
@Table(name = "execution_step_has_vm_user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExecutionStepHasVmUser.findAll",
            query = "SELECT e FROM ExecutionStepHasVmUser e")
    , @NamedQuery(name = "ExecutionStepHasVmUser.findByExecutionStepTestCaseExecutionId",
            query = "SELECT e FROM ExecutionStepHasVmUser e WHERE "
            + "e.executionStepHasVmUserPK.executionStepTestCaseExecutionId = :executionStepTestCaseExecutionId")
    , @NamedQuery(name = "ExecutionStepHasVmUser.findByExecutionStepStepId",
            query = "SELECT e FROM ExecutionStepHasVmUser e WHERE "
            + "e.executionStepHasVmUserPK.executionStepStepId = :executionStepStepId")
    , @NamedQuery(name = "ExecutionStepHasVmUser.findByExecutionStepStepTestCaseId",
            query = "SELECT e FROM ExecutionStepHasVmUser e WHERE "
            + "e.executionStepHasVmUserPK.executionStepStepTestCaseId = :executionStepStepTestCaseId")
    , @NamedQuery(name = "ExecutionStepHasVmUser.findByVmUserId",
            query = "SELECT e FROM ExecutionStepHasVmUser e WHERE "
            + "e.executionStepHasVmUserPK.vmUserId = :vmUserId")
    , @NamedQuery(name = "ExecutionStepHasVmUser.findByVmUserExecutionStepTestCaseExecutionId",
            query = "SELECT e FROM ExecutionStepHasVmUser e WHERE "
            + "e.executionStepHasVmUserPK.vmUserExecutionStepTestCaseExecutionId = :vmUserExecutionStepTestCaseExecutionId")
    , @NamedQuery(name = "ExecutionStepHasVmUser.findByVmUserExecutionStepStepId",
            query = "SELECT e FROM ExecutionStepHasVmUser e WHERE "
            + "e.executionStepHasVmUserPK.vmUserExecutionStepStepId = :vmUserExecutionStepStepId")
    , @NamedQuery(name = "ExecutionStepHasVmUser.findByVmUserExecutionStepStepTestCaseId",
            query = "SELECT e FROM ExecutionStepHasVmUser e WHERE "
            + "e.executionStepHasVmUserPK.vmUserExecutionStepStepTestCaseId = :vmUserExecutionStepStepTestCaseId")
    , @NamedQuery(name = "ExecutionStepHasVmUser.findByRoleId",
            query = "SELECT e FROM ExecutionStepHasVmUser e WHERE "
            + "e.executionStepHasVmUserPK.roleId = :roleId")})
public class ExecutionStepHasVmUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ExecutionStepHasVmUserPK executionStepHasVmUserPK;
    @JoinColumns({
        @JoinColumn(name = "execution_step_test_case_execution_id",
                referencedColumnName = "test_case_execution_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "execution_step_step_id",
                referencedColumnName = "step_id", insertable = false,
                updatable = false)
        , @JoinColumn(name = "execution_step_step_test_case_id",
                referencedColumnName = "step_test_case_id",
                insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private ExecutionStep executionStep;
    @JoinColumn(name = "role_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Role role;
    @JoinColumn(name = "vm_user_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private VmUser vmUser;

    public ExecutionStepHasVmUser() {
    }

    public ExecutionStepHasVmUser(ExecutionStepHasVmUserPK executionStepHasVmUserPK) {
        this.executionStepHasVmUserPK = executionStepHasVmUserPK;
    }

    public ExecutionStepHasVmUser(int executionStepTestCaseExecutionId,
            int executionStepStepId, int executionStepStepTestCaseId,
            int vmUserId, int vmUserExecutionStepTestCaseExecutionId,
            int vmUserExecutionStepStepId,
            int vmUserExecutionStepStepTestCaseId, int roleId) {
        this.executionStepHasVmUserPK
                = new ExecutionStepHasVmUserPK(executionStepTestCaseExecutionId,
                        executionStepStepId, executionStepStepTestCaseId,
                        vmUserId, vmUserExecutionStepTestCaseExecutionId,
                        vmUserExecutionStepStepId,
                        vmUserExecutionStepStepTestCaseId, roleId);
    }

    public ExecutionStepHasVmUserPK getExecutionStepHasVmUserPK() {
        return executionStepHasVmUserPK;
    }

    public void setExecutionStepHasVmUserPK(ExecutionStepHasVmUserPK executionStepHasVmUserPK) {
        this.executionStepHasVmUserPK = executionStepHasVmUserPK;
    }

    public ExecutionStep getExecutionStep() {
        return executionStep;
    }

    public void setExecutionStep(ExecutionStep executionStep) {
        this.executionStep = executionStep;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public VmUser getVmUser() {
        return vmUser;
    }

    public void setVmUser(VmUser vmUser) {
        this.vmUser = vmUser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (executionStepHasVmUserPK != null ? executionStepHasVmUserPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ExecutionStepHasVmUser)) {
            return false;
        }
        ExecutionStepHasVmUser other = (ExecutionStepHasVmUser) object;
        return !((this.executionStepHasVmUserPK == null
                && other.executionStepHasVmUserPK != null)
                || (this.executionStepHasVmUserPK != null
                && !this.executionStepHasVmUserPK
                        .equals(other.executionStepHasVmUserPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExecutionStepHasVmUser[ "
                + "executionStepHasVmUserPK=" + executionStepHasVmUserPK + " ]";
    }
}
