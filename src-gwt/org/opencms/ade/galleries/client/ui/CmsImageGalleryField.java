/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.ade.galleries.client.ui;

import org.opencms.ade.galleries.client.CmsGalleryFactory;
import org.opencms.ade.galleries.client.I_CmsGalleryWidgetHandler;
import org.opencms.ade.galleries.client.preview.CmsCroppingParamBean;
import org.opencms.gwt.client.CmsCoreProvider;
import org.opencms.gwt.client.I_CmsHasInit;
import org.opencms.gwt.client.ui.CmsPushButton;
import org.opencms.gwt.client.ui.I_CmsAutoHider;
import org.opencms.gwt.client.ui.I_CmsButton.ButtonStyle;
import org.opencms.gwt.client.ui.css.I_CmsImageBundle;
import org.opencms.gwt.client.ui.css.I_CmsLayoutBundle;
import org.opencms.gwt.client.ui.input.CmsSelectBox;
import org.opencms.gwt.client.ui.input.CmsTextArea;
import org.opencms.gwt.client.ui.input.I_CmsFormWidget;
import org.opencms.gwt.client.ui.input.form.CmsWidgetFactoryRegistry;
import org.opencms.gwt.client.ui.input.form.I_CmsFormWidgetFactory;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.CmsUUID;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A widget for selecting a resource from an ADE gallery dialog.<p>
 * 
 * @since 8.0.0
 */
public class CmsImageGalleryField extends Composite
implements I_CmsFormWidget, I_CmsHasInit, HasValueChangeHandlers<String> {

    /**
     * The UI Binder interface for this widget.<p>
     */
    protected interface I_CmsImageGalleryFieldUiBinder extends UiBinder<Panel, CmsImageGalleryField> {
        // binder interface
    }

    /** The widget type. */
    public static final String WIDGET_TYPE = "imageGallery";

    /** Parameter to split or generate the value string. */
    private static final String PARAMETER_DESC = "&description=";

    /** Parameter to split or generate the value string. */
    private static final String PARAMETER_FORMAT = "&format=";

    /** Parameter to split or generate the value string. */
    private static final String PARAMETER_SCALE = "?__scale=";

    /** The ui binder for this widget. */
    private static I_CmsImageGalleryFieldUiBinder uibinder = GWT.create(I_CmsImageGalleryFieldUiBinder.class);

    /** The scale parameters from popup. */
    protected CmsCroppingParamBean m_croppingParam;

    /** The text area. */
    @UiField
    protected CmsTextArea m_descriptionArea;

    /** The fading element. */
    @UiField
    protected Label m_fader;

    /** The select box. */
    @UiField
    protected CmsSelectBox m_formatSelection;

    /**The image priview field. */
    @UiField
    protected SimplePanel m_imageField;

    /** The button to to open the selection. */
    @UiField
    protected CmsPushButton m_opener;

    /** The gallery pop-up. */
    protected CmsGalleryPopup m_popup;

    /** The textbox containing the currently selected path. */
    @UiField
    protected TextBox m_textbox;

    /** Map of values for the Formats selection box. */
    Map<String, String> m_formats = new LinkedHashMap<String, String>();

    /** The image container. */
    Image m_image = new Image();

    /** The description value. */
    private String m_description;

    /** The start gallery path. */
    private String m_galleryPath;

    /** The gallery types. */
    private String m_galleryTypes;

    /** The image format names. */
    private String m_imageFormatNames;

    /** The image formats. */
    private String m_imageFormats;

    /** The reference path, for example the site path of the edited resource. */
    private String m_referencePath;

    /** The scale values. */
    private String m_scaleValue;

    /** The selected format. */
    private String m_selectedFormat;

    /** The resource types. */
    private String m_types;

    /** The use formats flag. */
    private boolean m_useFormats;

    /** 
     * Constructs a new gallery widget.<p>
     */
    public CmsImageGalleryField() {

        initWidget(uibinder.createAndBindUi(this));

        m_descriptionArea.getTextArea().setStyleName(I_CmsLayoutBundle.INSTANCE.globalWidgetCss().textAreaBox());
        m_descriptionArea.getTextAreaContainer().addStyleName(
            I_CmsLayoutBundle.INSTANCE.globalWidgetCss().textAreaBoxPanel());
        m_descriptionArea.setRows(3);
        m_descriptionArea.getTextAreaContainer().onResize();

        m_formatSelection.getOpener().addStyleName(I_CmsLayoutBundle.INSTANCE.globalWidgetCss().selectBoxSelected());
        m_formatSelection.getSelector().addStyleName(I_CmsLayoutBundle.INSTANCE.globalWidgetCss().selectBoxPopup());

        m_opener.setButtonStyle(ButtonStyle.TRANSPARENT, null);
        m_opener.setImageClass(I_CmsImageBundle.INSTANCE.style().popupIcon());

        m_imageField.add(m_image);
    }

    /** 
     * Constructs a new gallery widget.<p>
     * 
     * @param iconImage the icon image class 
     */
    public CmsImageGalleryField(String iconImage) {

        this();
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(iconImage)) {
            m_opener.setImageClass(iconImage);
        }
    }

    /**
     * Initializes this class.<p>
     */
    public static void initClass() {

        // registers a factory for creating new instances of this widget
        CmsWidgetFactoryRegistry.instance().registerFactory(WIDGET_TYPE, new I_CmsFormWidgetFactory() {

            /**
             * @see org.opencms.gwt.client.ui.input.form.I_CmsFormWidgetFactory#createWidget(java.util.Map)
             */
            public I_CmsFormWidget createWidget(Map<String, String> widgetParams) {

                CmsImageGalleryField galleryField = new CmsImageGalleryField(null);
                galleryField.parseConfiguration(widgetParams.get("configuration"));
                return galleryField;
            }
        });
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {

        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#getApparentValue()
     */
    public String getApparentValue() {

        return getFormValueAsString();
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#getFieldType()
     */
    public FieldType getFieldType() {

        return FieldType.STRING;
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#getFormValue()
     */
    public Object getFormValue() {

        return m_textbox.getValue();
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#getFormValueAsString()
     */
    public String getFormValueAsString() {

        String result = m_textbox.getValue();
        result += PARAMETER_SCALE + m_scaleValue;
        if (m_useFormats) {
            result += PARAMETER_FORMAT + m_formatSelection.getFormValueAsString();
            m_selectedFormat = m_formatSelection.getFormValueAsString();
        }
        result += PARAMETER_DESC + URL.encode(m_descriptionArea.getFormValueAsString());
        m_description = m_descriptionArea.getFormValueAsString();

        return result;
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#isEnabled()
     */
    public boolean isEnabled() {

        return m_textbox.isEnabled();
    }

    /**
     * On text box blur.<p>
     * @param event the event
     */
    @UiHandler("m_textbox")
    public void onBlur(BlurEvent event) {

        setFaded((m_textbox.getValue().length() * 6.88) > m_textbox.getOffsetWidth());
        setTitle(m_textbox.getValue());
        //CmsDomUtil.setCaretPosition(m_textbox.getElement(), 0);
    }

    /**
     * On fader click.<p>
     * 
     * @param event the event
     */
    @UiHandler("m_fader")
    public void onFaiderClick(ClickEvent event) {

        m_textbox.setFocus(true);
    }

    /**
     * On opener click.<p>
     * 
     * @param event the event
     */
    @UiHandler("m_opener")
    public void onOpenerClick(ClickEvent event) {

        openGalleryDialog();

    }

    /**
     * On select box value change.<p>
     * 
     * @param event the even
     */
    @UiHandler("m_formatSelection")
    public void onSelectBoxChange(ValueChangeEvent<String> event) {

        ValueChangeEvent.fire(CmsImageGalleryField.this, getFormValueAsString());
    }

    /**
     * On textarea box value change.<p>
     * 
     * @param event the even
     */
    @UiHandler("m_descriptionArea")
    public void onTextAreaBoxChange(ValueChangeEvent<String> event) {

        ValueChangeEvent.fire(CmsImageGalleryField.this, getFormValueAsString());
    }

    /**
     * On text box value change.<p>
     * 
     * @param event the even
     */
    @UiHandler("m_textbox")
    public void onTextboxChange(ValueChangeEvent<String> event) {

        ValueChangeEvent.fire(CmsImageGalleryField.this, getFormValueAsString());
    }

    /**
     * Internal method which opens the gallery dialog.<p>
     */
    public void openGalleryDialog() {

        if (m_popup == null) {
            m_popup = CmsGalleryFactory.createGalleryPopup(
                new I_CmsGalleryWidgetHandler() {

                    public void setWidgetValue(
                        String resourcePath,
                        CmsUUID structureId,
                        CmsCroppingParamBean croppingParameter) {

                        m_croppingParam = new CmsCroppingParamBean(croppingParameter);
                        String path = resourcePath + "?";
                        path += croppingParameter.toString();
                        path += PARAMETER_FORMAT + croppingParameter.getFormatName();
                        setValue(path, true);
                        m_popup.hide();
                    }
                },
                m_referencePath,
                m_galleryPath,
                getFormValueAsString(),
                m_types,
                m_galleryTypes,
                m_useFormats,
                m_imageFormats,
                m_imageFormatNames);
            m_popup.center();
        } else {
            m_popup.searchElement(getFormValueAsString());
        }
    }

    /**
     * Parses the widget configuration.<p>
     * 
     * @param configuration the widget configuration as a JSON string
     */
    public native void parseConfiguration(String configuration)/*-{
                                                               var config;
                                                               if (typeof JSON != 'undefined') {
                                                               config = JSON.parse(configuration);
                                                               } else {
                                                               config = eval("(" + configuration + ")");
                                                               }
                                                               if (config.types){
                                                               this.@org.opencms.ade.galleries.client.ui.CmsImageGalleryField::setTypes(Ljava/lang/String;)(config.types);
                                                               }
                                                               if (config.gallerypath){
                                                               this.@org.opencms.ade.galleries.client.ui.CmsImageGalleryField::setGalleryPath(Ljava/lang/String;)(config.gallerypath);
                                                               }
                                                               if (config.gallerytypes){
                                                               this.@org.opencms.ade.galleries.client.ui.CmsImageGalleryField::setGalleryTypes(Ljava/lang/String;)(config.gallerytypes);
                                                               }
                                                               if (config.referencepath){
                                                               this.@org.opencms.ade.galleries.client.ui.CmsImageGalleryField::setReferencePath(Ljava/lang/String;)(config.referencepath);
                                                               }
                                                               if (config.useFormats){
                                                               this.@org.opencms.ade.galleries.client.ui.CmsImageGalleryField::setUseFormats(Z)(config.useFormats);
                                                               }
                                                               if (config.imageFormats){
                                                               this.@org.opencms.ade.galleries.client.ui.CmsImageGalleryField::setImageFormats(Ljava/lang/String;)(config.imageFormats.toString());
                                                               }
                                                               if (config.imageFormatNames){
                                                               this.@org.opencms.ade.galleries.client.ui.CmsImageGalleryField::setImageFormatNames(Ljava/lang/String;)(config.imageFormatNames.toString());
                                                               }
                                                               
                                                               }-*/;

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#reset()
     */
    public void reset() {

        m_textbox.setValue("");
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#setAutoHideParent(org.opencms.gwt.client.ui.I_CmsAutoHider)
     */
    public void setAutoHideParent(I_CmsAutoHider autoHideParent) {

        // do nothing 
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {

        m_textbox.setEnabled(enabled);
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#setErrorMessage(java.lang.String)
     */
    public void setErrorMessage(String errorMessage) {

        // do nothing 
    }

    /**
     * Toggles the fading element.<p>
     * 
     * @param faded <code>true</code> to show the fading element.<p>
     */
    public void setFaded(boolean faded) {

        m_fader.setVisible(faded);
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#setFormValueAsString(java.lang.String)
     */
    public void setFormValueAsString(String value) {

        setValue(value, false);
    }

    /**
     * Sets the gallery opener button title.<p>
     * 
     * @param openerTitle the gallery opener button title
     */
    public void setGalleryOpenerTitle(String openerTitle) {

        m_opener.setTitle(openerTitle);
    }

    /**
     * Sets the galleryPath.<p>
     *
     * @param galleryPath the galleryPath to set
     */
    public void setGalleryPath(String galleryPath) {

        m_galleryPath = galleryPath;
    }

    /**
     * Sets the galleryTypes.<p>
     *
     * @param galleryTypes the galleryTypes to set
     */
    public void setGalleryTypes(String galleryTypes) {

        m_galleryTypes = galleryTypes;
    }

    /**
     * Sets the image format names.<p>
     *
     * @param imageFormatNames the image format names to set
     */
    public void setImageFormatNames(String imageFormatNames) {

        m_imageFormatNames = imageFormatNames;
        generatesFormatSelection();
    }

    /**
     * Sets the image formats.<p>
     *
     * @param imageFormats the image formats to set
     */
    public void setImageFormats(String imageFormats) {

        m_imageFormats = imageFormats;
    }

    /**
     * Sets the name of the input field.<p>
     * 
     * @param name of the input field
     * */
    public void setName(String name) {

        m_textbox.setName(name);
        m_descriptionArea.setName(name + "_TextArea");

    }

    /**
     * Sets the referencePath.<p>
     *
     * @param referencePath the referencePath to set
     */
    public void setReferencePath(String referencePath) {

        m_referencePath = referencePath;
    }

    /**
     * Sets the types.<p>
     *
     * @param types the types to set
     */
    public void setTypes(String types) {

        m_types = types;
    }

    /**
     * Sets the use image formats flag.<p>
     *
     * @param useFormats the use image formats flag to set
     */
    public void setUseFormats(boolean useFormats) {

        m_useFormats = useFormats;
    }

    /**
     * Sets the widget value.<p>
     * 
     * @param value the value to set
     * @param fireEvent if the change event should be fired
     */
    public void setValue(String value, boolean fireEvent) {

        value = splitValue(value);
        m_textbox.setValue(value);
        if (fireEvent) {
            ValueChangeEvent.fire(this, getFormValueAsString());
        }
    }

    /***/
    private void generatesFormatSelection() {

        if (m_useFormats) {
            String[] formats = m_imageFormatNames.split(",");
            for (int i = 0; i < formats.length; i++) {
                m_formats.put(formats[i].split(":")[0], formats[i].split(":")[1]);
            }
            m_formatSelection.setItems(m_formats);
        } else {
            m_formatSelection.removeFromParent();
            m_descriptionArea.setRowsGallery(4);
        }
    }

    /**
     * Splits the value in all it�s informations.<p>
     * First part is the URL of the image. The second one describes the scale of this image.<p>
     * The last one sets the selected format.<p>
     * 
     * @param value that should be split
     * @return the URL of the image without any parameters
     */

    private String splitValue(String value) {

        m_croppingParam = CmsCroppingParamBean.parseImagePath(value);
        int indexofscale = value.indexOf(PARAMETER_SCALE);
        if (indexofscale > -1) {
            String scal = "";
            int hasmoreValues = value.lastIndexOf("&");
            if (hasmoreValues > indexofscale) {

                scal = value.substring(indexofscale, value.indexOf("&")).replace(PARAMETER_SCALE, "");

            } else {
                scal = value.substring(indexofscale).replace(PARAMETER_SCALE, "");
            }
            if (!scal.equals(m_scaleValue)) {
                m_scaleValue = scal;
            }
            value = value.replace(PARAMETER_SCALE + m_scaleValue, "");

        }
        int indexofformat = value.indexOf(PARAMETER_FORMAT);
        if (indexofformat > -1) {
            int hasmoreValues = value.lastIndexOf("&");
            if (hasmoreValues > indexofformat) {
                m_selectedFormat = value.substring(indexofformat, value.indexOf(PARAMETER_DESC)).replace(
                    PARAMETER_FORMAT,
                    "");
            } else {
                m_selectedFormat = value.substring(indexofformat).replace(PARAMETER_FORMAT, "");
            }
            value = value.replace(PARAMETER_FORMAT + m_selectedFormat, "");
            m_formatSelection.selectValue(m_selectedFormat);
        }
        int indexofdescritption = value.indexOf(PARAMETER_DESC);
        if (indexofdescritption > -1) {
            m_description = value.substring(indexofdescritption).replace(PARAMETER_DESC, "");
            value = value.replace(PARAMETER_DESC + m_description, "");
            m_description = URL.decode(m_description);
            m_descriptionArea.setFormValueAsString(m_description);
        }
        if (!value.isEmpty()) {
            String imageLink = CmsCoreProvider.get().link(value);
            CmsCroppingParamBean restricted = m_croppingParam.getRestrictedSizeParam(110, 80);
            m_image.setUrl(imageLink + "?" + restricted);
            m_image.getElement().getStyle().setMarginTop((110 - restricted.getTargetHeight()) / 2, Unit.PX);
        }
        return value;

    }

}
