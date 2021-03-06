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
 *
 */


import groovy.json.JsonException
import groovy.json.JsonSlurper
import org.apache.commons.lang3.StringUtils
import org.craftercms.studio.api.v1.exception.security.PasswordDoesNotMatchException
import org.craftercms.studio.api.v1.exception.security.UserExternallyManagedException
import scripts.api.SecurityServices

def result = [:]
try {
    def requestBody = request.reader.text

    def slurper = new JsonSlurper()
    def parsedReq = slurper.parseText(requestBody)

    def username = parsedReq.username
    def current = parsedReq.current
    def newPassword = parsedReq.new

/** Validate Parameters */
    def invalidParams = false;
    def paramsList = []

// username
    try {
        if (StringUtils.isEmpty(username)) {
            invalidParams = true
            paramsList.add("username")
        }
    } catch (Exception exc) {
        invalidParams = true
        paramsList.add("username")
    }

// current password
    try {
        if (StringUtils.isEmpty(current)) {
            invalidParams = true
            paramsList.add("current")
        }
    } catch (Exception exc) {
        invalidParams = true
        paramsList.add("current")
    }

// new password
    try {
        if (StringUtils.isEmpty(newPassword)) {
            invalidParams = true
            paramsList.add("new")
        }
    } catch (Exception exc) {
        invalidParams = true
        paramsList.add("new")
    }

    if (invalidParams) {
        response.setStatus(400)
        result.message = "Invalid parameter(s): " + paramsList
    } else {
        def context = SecurityServices.createContext(applicationContext, request)
        try {
            def success = SecurityServices.changePassword(context, username, current, newPassword)
            if (success) {
                def locationHeader = request.getRequestURL().toString().replace(request.getPathInfo().toString(), "") + "/api/1/services/api/1/user/get.json?username=" + username
                response.addHeader("Location", locationHeader)
                result.message = "OK"
                response.setStatus(200)
            } else {
                response.setStatus(500)
                result.message = "Internal server error"
            }
        } catch (PasswordDoesNotMatchException e) {
            response.setStatus(401)
            result.message = "Unauthorized"
        } catch (UserExternallyManagedException e) {
            response.setStatus(403)
            result.message = "Externally managed user"
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