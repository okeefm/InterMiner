package org.intermine.web.bio.displayer;

/*
 * Copyright (C) 2002-2011 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import org.intermine.api.InterMineAPI;

/**
 * Displayer for similarity between genes based on KEGG pathways
 * @author Michael O'Keefe
 *
 */
public class KeggSimilarityDisplayer extends ReportDisplayer
{
    protected static final Logger LOG = Logger.getLogger(KeggSimilarityDisplayer.class);

    public KeggSimilarityDisplayer(ReportDisplayerconfig config, InterMineAPI im) {
        super(config, im);
    }

    @Override
    public void display(HttpServletRequest request, ReportObject reportObject) {

    }
}
