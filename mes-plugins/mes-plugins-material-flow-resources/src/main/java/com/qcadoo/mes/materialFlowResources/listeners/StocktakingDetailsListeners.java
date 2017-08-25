package com.qcadoo.mes.materialFlowResources.listeners;

import com.lowagie.text.DocumentException;
import com.qcadoo.mes.materialFlowResources.constants.StocktakingFields;
import com.qcadoo.mes.materialFlowResources.print.StocktakingReportService;
import com.qcadoo.mes.materialFlowResources.print.helper.ResourceDataProvider;
import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.FormComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class StocktakingDetailsListeners {

    private static final String L_FORM = "form";

    @Autowired
    private ResourceDataProvider resourceDataProvider;

    @Autowired
    private StocktakingReportService reportService;

    public void generate(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        state.performEvent(view, "save", new String[0]);

        if (state.isHasError()) {
            return;
        }
        FormComponent form = (FormComponent) view.getComponentByReference(L_FORM);
        Entity report = form.getEntity();
        Entity reportDb = report.getDataDefinition().get(report.getId());
        reportDb.setField(StocktakingFields.GENERATED, Boolean.TRUE);
        reportDb.setField("generationDate", new Date());
        reportDb = reportDb.getDataDefinition().save(reportDb);
        try {
            reportService.generateReport(state, reportDb);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        state.performEvent(view, "reset", new String[0]);

    }

    public void print(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        reportService.printReport(view, state);
    }

}