package org.intermine.bio.item.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intermine.bio.chado.CVService;
import org.intermine.bio.chado.OrganismService;
import org.intermine.bio.chado.PhenotypeService;
import org.intermine.bio.chado.StockService;
import org.intermine.bio.dataconversion.ChadoDBConverter;
import org.intermine.bio.dataconversion.DataSourceProcessor;
import org.intermine.bio.dataflow.config.ApplicationContext;
import org.intermine.bio.item.ItemProcessor;
import org.intermine.bio.item.util.ItemHolder;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.xml.full.Item;
import org.intermine.bio.domain.source.*;

public class PhenotypeItemProcessor extends DataSourceProcessor implements ItemProcessor<SourcePhenotype, Item> {

	protected static final Logger log = Logger.getLogger(PhenotypeItemProcessor.class);

	private String targetClassName;

	private static final String ITEM_CLASSNAME = "Phenotype";

	public PhenotypeItemProcessor(ChadoDBConverter chadoDBConverter) {
		super(chadoDBConverter);
	}

	@Override
	public Item process(SourcePhenotype item) throws Exception {

		return createItem(item);

	}

	private Item createItem(SourcePhenotype source) throws ObjectStoreException {

		Exception exception = null;

		Item item = null;

		int itemId = -1;

		try {
			log.info("Creating Item has started. Source Object:" + source);

			item = super.getService().createItem(ITEM_CLASSNAME);

			log.info("Item place holder has been created: " + item);

			log.info("Phenotype Unique Accession: " + source.getUniqueAccession());
			item.setAttribute("primaryIdentifier", source.getUniqueAccession());

			if (!StringUtils.isBlank(source.getName())) {
				log.info("Phenotype Name/Secondary Identifier: " + source.getName());
				item.setAttribute("secondaryIdentifier", source.getName());
			}

			if (!StringUtils.isBlank(source.getDescription())) {
				log.info("Phenotype Description:" + source.getDescription());
				item.setAttribute("description", source.getDescription());
			}

			itemId = super.getService().store(item);

		} catch (ObjectStoreException e) {
			exception = e;
		} catch (Exception e) {
			exception = e;
		} finally {

			if (exception != null) {
				log.error("Error storing item for source record:" + source);
			} else {
				log.info("Target Item has been created. Target Object:" + item);

				ItemHolder itemHolder = new ItemHolder(item, itemId);

				if (itemHolder != null && itemId != -1) {
					PhenotypeService.addPhenotypeItem(source.getUniqueAccession(), itemHolder);
				}

			}
		}
		return item;
	}

	public void setTargetClassName(String name) {
		this.targetClassName = name;
	}

	public String getTargetClassName() {
		return this.targetClassName;
	}

}
