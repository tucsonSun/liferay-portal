/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.workflow.web.internal.portlet.action;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionManagerUtil;
import com.liferay.portal.workflow.web.internal.constants.WorkflowPortletKeys;

import java.text.DateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW,
		"mvc.command.name=revertWorkflowDefinition"
	},
	service = MVCActionCommand.class
)
public class RevertWorkflowDefinitionMVCActionCommand
	extends UpdateWorkflowDefinitionMVCActionCommand {

	/**
	 * Adds a success message to the workflow definition reversion action
	 *
	 * @param  actionRequest The actionRequest object of the action
	 * @review
	 */
	@Override
	protected void addSuccessMessage(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Locale locale = themeDisplay.getLocale();

		DateFormat dateTimeFormat = null;

		if (DateUtil.isFormatAmPm(locale)) {
			dateTimeFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				"MMM d, yyyy, hh:mm a", locale);
		}
		else {
			dateTimeFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				"MMM d, yyyy, HH:mm", locale);
		}

		Date previousDefinitionModifiedDate = ParamUtil.getDate(
			actionRequest, "previousDefinitionModifiedDate", dateTimeFormat);

		String dateTime = dateTimeFormat.format(previousDefinitionModifiedDate);

		ResourceBundle resourceBundle =
			_resourceBundleLoader.loadResourceBundle(locale);

		SessionMessages.add(
			actionRequest, "requestProcessed",
			LanguageUtil.format(
				resourceBundle, "restored-to-revision-from-x", dateTime));
	}

	/**
	 * Reverts a workflow definition to the published state, creating a new
	 * version of it.
	 *
	 * @param  actionRequest
	 * @param  actionResponse
	 * @review
	 */
	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String name = ParamUtil.getString(actionRequest, "name");
		int version = ParamUtil.getInteger(actionRequest, "version");

		WorkflowDefinition previousDefinitionRevision =
			WorkflowDefinitionManagerUtil.getWorkflowDefinition(
				themeDisplay.getCompanyId(), name, version);

		actionRequest.setAttribute(
			"previousDefinitionModifiedDate",
			previousDefinitionRevision.getModifiedDate());

		String content = previousDefinitionRevision.getContent();

		WorkflowDefinition workflowDefinition =
			workflowDefinitionManager.deployWorkflowDefinition(
				themeDisplay.getCompanyId(), themeDisplay.getUserId(),
				previousDefinitionRevision.getTitle(), content.getBytes());

		setRedirectAttribute(actionRequest, workflowDefinition);

		sendRedirect(actionRequest, actionResponse);
	}

	@Reference(
		target = "(bundle.symbolic.name=com.liferay.portal.workflow.web)",
		unbind = "-"
	)
	private ResourceBundleLoader _resourceBundleLoader;

}