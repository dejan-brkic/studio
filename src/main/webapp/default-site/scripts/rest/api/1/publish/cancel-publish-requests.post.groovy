/*
 * Crafter Studio Web-content authoring solution
 * Copyright (C) 2007-2017 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import groovy.json.JsonException
import groovy.json.JsonSlurper
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.craftercms.studio.api.v1.exception.SiteNotFoundException
import scripts.api.DeploymentServices

def result = [:]
def requestBody = request.reader.text
try {
    def slurper = new JsonSlurper()
    def parsedReq = slurper.parseText(requestBody)

    def siteId = parsedReq.site_id
    def packageIds = parsedReq.package_ids
    /** Validate Parameters */
    def invalidParams = false;
    def paramsList = []

    try {
        if (StringUtils.isEmpty(siteId)) {
            invalidParams = true
            paramsList.add("site_id")
        }
    } catch (Exception exc) {
        invalidParams = true
        paramsList.add("site_id")
    }

    try {
        if (CollectionUtils.isEmpty(packageIds)) {
            invalidParams = true
            paramsList.add("package_ids")
        }
    } catch (Exception exc) {
        invalidParams = true
        paramsList.add("package_ids")
    }

    if (invalidParams) {
        response.setStatus(400)
        result.message = "Invalid parameter(s): " + paramsList
    } else {
        def context = DeploymentServices.createContext(applicationContext, request)
        try {
            def status = DeploymentServices.cancelPublishRequests(context, siteId)
            if (status != null) {
                response.setStatus(200)
                result = status
            } else {
                response.setStatus(500)
                result.message = "Internal server error"
            }
        } catch (SiteNotFoundException e) {
            response.setStatus(404)
            result.message = "Site not found"
        } catch (Exception e) {
            response.setStatus(500)
            result.message = "Internal server error: \n" + e
        }
    }
} catch (JsonException e) {
    response.setStatus(400)
    result.message = "Bad Request"
}
return result

