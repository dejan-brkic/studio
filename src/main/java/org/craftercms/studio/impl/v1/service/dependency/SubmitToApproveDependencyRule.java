/*
 * Crafter Studio Web-content authoring solution
 * Copyright (C) 2007-2015 Crafter Software Corporation.
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

package org.craftercms.studio.impl.v1.service.dependency;

import org.apache.commons.lang.StringUtils;
import org.craftercms.studio.api.v1.constant.DmConstants;
import org.craftercms.studio.api.v1.exception.ServiceException;
import org.craftercms.studio.api.v1.log.Logger;
import org.craftercms.studio.api.v1.log.LoggerFactory;
import org.craftercms.studio.api.v1.service.content.ContentService;
import org.craftercms.studio.api.v1.service.dependency.DependencyRule;
import org.craftercms.studio.api.v1.service.dependency.DependencyService;
import org.craftercms.studio.api.v1.service.objectstate.ObjectStateService;
import org.craftercms.studio.api.v1.to.ContentItemTO;
import org.craftercms.studio.impl.v1.util.ContentUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubmitToApproveDependencyRule implements DependencyRule {

    private final static Logger logger = LoggerFactory.getLogger(SubmitToApproveDependencyRule.class);

    @Override
    public Set<String> applyRule(String site, String path) throws ServiceException {
        Set<String> dependencies = new HashSet<String>();
        List<String> allDependencies = new ArrayList<String>();
        getMandatoryParent(site, path, allDependencies);
        getAllDependenciesRecursive(site, path, allDependencies);
        dependencies.addAll(allDependencies);
        return dependencies;
    }

    protected void getMandatoryParent(String site, String path, List<String> dependecyPaths) {
        int idx = path.lastIndexOf("/" + DmConstants.INDEX_FILE);
        if (idx > 0) {
            path = path.substring(0, idx);
        }
        String parentPath = ContentUtils.getParentUrl(path);
        if (StringUtils.isNotEmpty(parentPath)) {
            if (contentService.contentExists(site, parentPath)) {
                ContentItemTO item = contentService.getContentItem(site, parentPath);
                if (item.isNew()) {
                    dependecyPaths.add(item.getUri());
                    getMandatoryParent(site, item.getUri(), dependecyPaths);
                }
            }
        }
    }

    protected void getAllDependenciesRecursive(String site, String path, List<String> dependecyPaths) throws ServiceException {
        Set<String> depPaths = dependencyService.getItemDependencies(site, path, 1);
        for (String depPath : depPaths) {
            if (!dependecyPaths.contains(depPath)) {
                if (objectStateService.isNew(site, depPath)) {
                    dependecyPaths.add(depPath);
                    getAllDependenciesRecursive(site, depPath, dependecyPaths);
                } else {
                    if (objectStateService.isUpdated(site, depPath)) {
                        for (String contentSpecificDependency : contentSpecificDependencies) {
                            Pattern p = Pattern.compile(contentSpecificDependency);
                            Matcher m = p.matcher(depPath);
                            if (m.matches()) {
                                dependecyPaths.add(depPath);
                                getAllDependenciesRecursive(site, depPath, dependecyPaths);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public ObjectStateService getObjectStateService() { return objectStateService; }
    public void setObjectStateService(ObjectStateService objectStateService) { this.objectStateService = objectStateService; }

    public DependencyService getDependencyService() { return dependencyService; }
    public void setDependencyService(DependencyService dependencyService) { this.dependencyService = dependencyService; }

    public List<String> getContentSpecificDependencies() { return contentSpecificDependencies; }
    public void setContentSpecificDependencies(List<String> contentSpecificDependencies) { this.contentSpecificDependencies = contentSpecificDependencies; }

    public ContentService getContentService() { return contentService; }
    public void setContentService(ContentService contentService) { this.contentService = contentService; }

    protected ObjectStateService objectStateService;
    protected DependencyService dependencyService;
    protected List<String> contentSpecificDependencies;
    protected ContentService contentService;
}