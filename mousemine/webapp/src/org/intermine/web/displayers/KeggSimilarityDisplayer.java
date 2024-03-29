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
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
    private Map<String, Integer[]> pathwayCache = new HashMap<String, Integer[]>();
    private ArrayList<String> pathwayNames = new ArrayList<String>();
    private static final Logger LOG = Logger.getLogger(KeggSimilarityDisplayer.class);

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
            Model model = im.getModel();
            PathQueryExecutor executor = im.getPathQueryExecutor(profile);
            
            InterMineObject object = (InterMineObject) request.getAttribute("object");
            String primaryIdentifier, name = null;
            try {
                primaryIdentifier = (String) object.getFieldValue("primaryIdentifier");
            } catch (IllegalAccessException e) {
                return;
            }
            if (StringUtils.isEmpty(primaryIdentifier)) {
                request.setAttribute("primaryIdentifier", "empty");
                return;
            } else {
                request.setAttribute("primaryIdentifier", primaryIdentifier);
                if (pathwayNames.size() == 0) {
                    pathwayNames = getAllPathways(profile);
                }
                request.setAttribute("pathwayNames", pathwayNames.subList(0, 20));
            }
        }

    }
    
    private ArrayList <String> getAllPathways(Profile profile) {
        //LOG.error("Enters getAllPathways");
        PathQuery q = new PathQuery(im.getModel());
        q.addViews("Pathway.identifier");
        q.addOrderBy("Pathway.identifier", OrderDirection.ASC);
        PathQueryExecutor executor = im.getPathQueryExecutor(profile);
        ExportResultsIterator it = executor.execute(q);
        ArrayList<String> pathways = new ArrayList<String>();
        List <ResultElement> row = new ArrayList<ResultElement>();
        while (it.hasNext()) {
            row = it.next();
            LOG.error("Row 0 field: " + (String) row.get(0).getField());
            pathways.add((String) row.get(0).getField());
        }
        return pathways;
    }
    
    private Integer[] getKeggPathwaysForGene(String organismName, String gene, Profile profile) {
        PathQuery q = new PathQuery(im.getModel());
        q.addViews("Gene.pathways.identifier");
        q.addConstraint(Constraints.eq("Gene.primaryIdentifier", gene));
        q.addConstraint(Constraints.eq("Gene.organism.name", organismName));
        q.addOrderBy("Gene.pathways.identifier", OrderDirection.ASC);
        PathQueryExecutor executor = im.getPathQueryExecutor(profile);
        ExportResultsIterator it = executor.execute(q);
        ArrayList<Integer> pathways = new ArrayList<Integer>();
        int i = 0;
        List <ResultElement> row = it.next();
        while (it.hasNext()) {
            if (pathwayNames.get(i) == (String) row.get(0).getField()) {
                row = it.next();
                pathways.add(1);
                i++;
            } else {
                pathways.add(0);
                i++;
            }
        }
        return (Integer[]) pathways.toArray();
    }
    
    private ArrayList<String> getAllGenesForOrganism(String organismName, Profile profile) {
        if (!StringUtils.isBlank(organismName)) {
            PathQuery q = new PathQuery(im.getModel());
            q.addViews("Gene.primaryIdentifier");
            q.addConstraint(Constraints.eq("Gene.organism.name", organismName));
            q.addOrderBy("Gene.primaryIdentifier", OrderDirection.ASC);
            PathQueryExecutor executor = im.getPathQueryExecutor(profile);
            ExportResultsIterator it = executor.execute(q, 0, 100);
            ArrayList<String> genes = new ArrayList<String>();
            List <ResultElement> row = new ArrayList<ResultElement>();
            while (it.hasNext()) {
                row = it.next();
                genes.add((String) row.get(0).getField());
            }
            return genes;
        }
        return null;
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
