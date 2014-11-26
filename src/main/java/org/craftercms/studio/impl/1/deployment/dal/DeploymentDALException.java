/*******************************************************************************
 * Crafter Studio Web-content authoring solution
 *     Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.craftercms.cstudio.impl.service.deployment.dal;


import org.craftercms.studio.api.v1.service.deployment.DeploymentException;

public class DeploymentDALException extends DeploymentException {

    private static final long serialVersionUID = -4662862862819359673L;

    public DeploymentDALException() {
        super();
    }

    public DeploymentDALException(String message) {
        super(message);
    }

    public DeploymentDALException(String message, Throwable t) {
        super(message, t);
    }
}