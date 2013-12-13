package org.gvt.model.basicsif;

import org.biopax.paxtools.io.sif.BinaryInteractionType;
import org.biopax.paxtools.pattern.miner.SIFType;
import org.gvt.model.biopaxl3.BioPAXEdge;
import org.gvt.model.NodeModel;
import org.gvt.model.sifl3.SIFEdge;

import java.util.*;

/**
 * @author Ozgun Babur
 *
 * Copyright: Bilkent Center for Bioinformatics, 2007 - present
 */
public class BasicSIFEdge extends BioPAXEdge
{
	protected SIFEdge.EdgeType type;

	protected Set<String> mediators;

	public BasicSIFEdge(NodeModel source, NodeModel target, String tag, String mediators)
	{
		super(source, target);

		this.type = SIFEdge.typeMap.get(tag);

		setTooltipText(tag);

		setColor(type.getColor());

		if (type.isDirected())
		{
			setArrow("Target");
		}

		if (!type.isSolid())
		{
			setStyle("Dashed");
		}

		this.mediators = new HashSet<String>();
		if (mediators != null)
		{
			Collections.addAll(this.mediators, mediators.split(" "));
		}
	}

	public BasicSIFEdge(BioPAXEdge excised, Map<NodeModel, NodeModel> map)
	{
		super(excised, map);
		this.type = ((BasicSIFEdge) excised).type;
		this.mediators = ((BasicSIFEdge) excised).mediators;
	}

	public int getSign()
	{
		return 0;
	}

	public boolean isDirected()
	{
		return type.getIntType().isDirected();
	}

	public boolean isBreadthEdge()
	{
		return true;
	}

	public String getIDHash()
	{
		return "";
	}

	public SIFType getType()
	{
		return type.getIntType();
	}

	public Set<String> getMediators()
	{
		return mediators;
	}

	public void addMediators(Collection<String> meds)
	{
		this.mediators.addAll(meds);
	}

	public List<String[]> getInspectable()
	{
		List<String[]> list = super.getInspectable();
		list.add(new String[]{"Type", type.getIntType().getTag()});
		return list;
	}
}