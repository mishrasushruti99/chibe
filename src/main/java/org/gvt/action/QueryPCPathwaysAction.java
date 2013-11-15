package org.gvt.action;

import cpath.client.CPathClient;
import cpath.client.util.CPathException;
import cpath.query.CPathSearchQuery;
import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;
import org.biopax.paxtools.model.BioPAXLevel;
import org.biopax.paxtools.model.Model;
import org.eclipse.jface.dialogs.MessageDialog;
import org.gvt.ChisioMain;
import org.gvt.gui.AbstractQueryParamDialog;
import org.gvt.gui.ItemSelectionDialog;
import org.gvt.gui.StringInputDialog;
import org.gvt.model.basicsif.BasicSIFGraph;
import org.patika.mada.graph.GraphObject;

import java.util.*;

/**
 * @author Ozgun Babur
 *
 */
public class QueryPCPathwaysAction extends QueryPCAction
{
	String keyword;
	
	String pathwayID;

	public QueryPCPathwaysAction(ChisioMain main)
	{
		super(main, "Pathways With Keyword ...", false, false);
	}

	public void run()
	{
        if(main.getOwlModel() == null || main.getOwlModel().getLevel().equals(BioPAXLevel.L3))
        {
            try
            {
                StringInputDialog dialog = new StringInputDialog(main.getShell(), "Query Pathways",
                    "Enter a keyword for pathway name", keyword,
                    "Find pathways related to the specified keyword");

                keyword = dialog.open();

                if (keyword == null || keyword.trim().length() == 0)
                {
                    return;
                }

                keyword = keyword.trim().toLowerCase();

                main.lockWithMessage("Querying Pathway Commons ...");

				SearchResponse resp = getPCSearchQuery().
					typeFilter("Pathway").
					queryString("name:" + keyword).
					result();

                main.unlock();

				if (resp != null)
				{
					List<Holder> holders = extractResultFromServResp(resp, keyword);

					ItemSelectionDialog isd = new ItemSelectionDialog(main.getShell(),
						500, "Result Pathways", "Select Pathway to Get", holders, null,
						false, true, null);
					isd.setDoSort(false);

					Object selected = isd.open();

					if (selected == null) return;

					pathwayID = ((Holder) selected).id;

					execute();
				}
				else
				{
					MessageDialog.openInformation(main.getShell(), "No result",
						"Could not find any match.\n");
				}
            }
            catch (Exception e)
            {
                e.printStackTrace();
                MessageDialog.openError(main.getShell(), "Error",
                    "An error occured during querying:\n" + e.getMessage());
            }
            finally
            {
                main.unlock();
                pathwayID = null;
                keyword = null;
            }
        }
        else
        {
            MessageDialog.openError(main.getShell(), "Incompatible Levels","This query is only applicable to Level 3 models.");
        }
	}
	
	private List<Holder> extractResultFromServResp(SearchResponse resp, String keyword)
	{
		List<Holder> holders = new ArrayList<Holder>();
//		Set<String> words = getKeywords(keyword);

		for (SearchHit hit : resp.getSearchHit())
		{
//			String name = hit.getName().toLowerCase();

//			for (String word : words)
			{
//				if (name.contains(word))
				{
					Holder h = new Holder(hit.getName(), hit.getUri());
//					if (!holders.contains(h))
						holders.add(h);
//					break;
				}
			}
		}
		return holders;
	}
	
	private List<String> getIDList(List<Holder> holders)
	{
		List<String> ids = new ArrayList<String>(holders.size());
		for (Holder holder : holders)
		{
			ids.add(holder.id);
		}
		return ids;
	}

	private Set<String> getKeywords(String keyword)
	{
		Set<String> set = new HashSet<String>();
		for (String s : keyword.split(" "))
		{
			s = s.trim();
			if (s.length() > 1) set.add(s);
		}
		return set;
	}
	
	@Override
	protected Model doQuery() throws CPathException
	{
		return getPCGetQuery().sources(new String[]{pathwayID}).result();
	}

	@Override
	protected Collection<GraphObject> doSIFQuery(BasicSIFGraph graph) throws CPathException
	{
		throw new UnsupportedOperationException("Cannot get from SIF");
	}

	@Override
	protected AbstractQueryParamDialog getDialog()
	{
		return null;
	}

	@Override
	protected boolean canQuery()
	{
		return pathwayID != null && pathwayID.length() > 0;
	}

	private class Holder implements Comparable
	{
		String name;
		String id;

		private Holder(String name, String id)
		{
			this.name = name;
			this.id = id;
		}

		@Override
		public int compareTo(Object o)
		{
			return name.compareTo(((Holder) o).name);
		}

		@Override
		public int hashCode()
		{
			return id.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof Holder)
			{
				Holder h = (Holder) o;
				return id.equals(h.id);
			}
			return false;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}
