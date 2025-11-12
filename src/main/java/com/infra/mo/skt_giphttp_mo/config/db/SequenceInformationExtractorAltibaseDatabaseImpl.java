package com.infra.mo.skt_giphttp_mo.config.db;

import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceInformationExtractorAltibaseDatabaseImpl extends SequenceInformationExtractorLegacyImpl {
    /**
     * Singleton access
     */
    public static final SequenceInformationExtractorAltibaseDatabaseImpl INSTANCE = new SequenceInformationExtractorAltibaseDatabaseImpl();

    @Override
    protected String sequenceNameColumn() {
        return "SEQUENCE_NAME";
    }

    @Override
    protected String sequenceCatalogColumn() {
        return null;
    }

    @Override
    protected String sequenceSchemaColumn() {
        return null;
    }

    @Override
    protected String sequenceStartValueColumn() {
        return "START_VALUE";
    }

    @Override
    protected String sequenceMinValueColumn() {
        return "MIN_VALUE";
    }

    @Override
    protected String sequenceMaxValueColumn() {
        return "MAX_VALUE";
    }

    @Override
    protected String sequenceIncrementColumn() {
        return "INCREMENT_BY";
    }
}