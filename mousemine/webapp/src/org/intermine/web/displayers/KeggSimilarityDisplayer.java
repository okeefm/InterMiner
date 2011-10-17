package org.intermine.bio.web.displayer;

/*
 * Copyright (C) 2002-2011 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.intermine.api.InterMineAPI;
import org.intermine.api.profile.Profile;
import org.intermine.api.query.PathQueryExecutor;
import org.intermine.api.results.ExportResultsIterator;
import org.intermine.api.results.ResultElement;
import org.intermine.metadata.Model;
import org.intermine.model.InterMineObject;
import org.intermine.model.bio.BioEntity;
import org.intermine.model.bio.Organism;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;
import org.intermine.web.displayer.ReportDisplayer;
import org.intermine.web.logic.config.ReportDisplayerConfig;
import org.intermine.web.logic.results.ReportObject;
import org.intermine.web.logic.session.SessionMethods;

/**
 * 
 * @author Michael O'Keefe
 */
public class KeggSimilarityDisplayer extends ReportDisplayer
{
    private Map<String, Boolean> organismCache = new HashMap<String, Boolean>();

    /**
     * Construct with config and the InterMineAPI.
     * @param config to describe the report displayer
     * @param im the InterMine API
     */
    public KeggSimilarityDisplayer(ReportDisplayerConfig config, InterMineAPI im) {
        super(config, im);
    }

    @Override
    public void display(HttpServletRequest request, ReportObject reportObject) {
        Profile profile = SessionMethods.getProfile(request.getSession());
        
        //checks if KEGG Pathways are loaded for the organism in question
        boolean keggLoadedForOrganism = true;
        
        //gets the organism name being queried
        String organismName = getOrganismName(reportObject);
        
        if (organismName != null) {
            keggLoadedForOrganism = isKeggLoadedForOrganism(organismName, profile);
        }
        if (!keggLoadedForOrganism) {
            String noKeggMessage = "No KEGG Pathways loaded for " + organismName;
            request.setAttribute("noKeggMessage", noKeggMessage);
        } else {
            
        }

        return;
    }
    
    private String getOrganismName(ReportObject reportObject) {
        Organism organism = ((BioEntity) reportObject.getObject()).getOrganism();
        if (organism != null) {
            if (!StringUtils.isBlank(organism.getName())) {
                return organism.getName();
            } else if (organism.getTaxonId() != null) {
                return "" + organism.getTaxonId();
            }
        }
        return null;
    }
    
    private boolean isKeggLoadedForOrganism(String organismField, Profile profile) {
        if (!organismCache.containsKey(organismField)) {
            PathQuery q = new PathQuery(im.getModel());
            q.addViews("Gene.pathways.identifier");
            if (StringUtils.isNumeric(organismField)) {
                q.addConstraint(Constraints.eq("Gene.organism.taxonId", organismField));
            } else {
                q.addConstraint(Constraints.eq("Gene.organism.name", organismField));
            }
            PathQueryExecutor executor = im.getPathQueryExecutor(profile);
            ExportResultsIterator result = executor.execute(q, 0, 1);
            organismCache.put(organismField, Boolean.valueOf(result.hasNext()));
        }
        return organismCache.get(organismField).booleanValue();
    }
}
