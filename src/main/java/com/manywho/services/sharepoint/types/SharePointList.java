package com.manywho.services.sharepoint.types;

import com.manywho.sdk.entities.draw.elements.type.*;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.types.AbstractType;

public class SharePointList extends AbstractType {
    public final static String NAME = "SharePointList";

    @Override
    public TypeElementBindingCollection getBindings() {
        return new TypeElementBindingCollection() {{
            add(new TypeElementBinding(NAME, "Details about a SharePointList", NAME, new TypeElementPropertyBindingCollection() {{
                add(new TypeElementPropertyBinding("ID", "ID"));
                add(new TypeElementPropertyBinding("Created Date Time", "Created Date Time"));
                add(new TypeElementPropertyBinding("Last Modified Date Time", "Description"));
                add(new TypeElementPropertyBinding("Description", "Description"));
                add(new TypeElementPropertyBinding("Name", "Name"));
                add(new TypeElementPropertyBinding("Web URL", "Web URL"));
                add(new TypeElementPropertyBinding("Site ID", "Site ID"));
            }}));
        }};
    }

    @Override
    public String getDeveloperName() {
        return SharePointList.NAME;
    }

    @Override
    public TypeElementPropertyCollection getProperties() {
        return new TypeElementPropertyCollection() {{
            add(new TypeElementProperty("ID", ContentType.String));
            add(new TypeElementProperty("Created Date Time", ContentType.DateTime));
            add(new TypeElementProperty("Last Modified Date Time", ContentType.DateTime));
            add(new TypeElementProperty("Description", ContentType.String));
            add(new TypeElementProperty("Name", ContentType.String));
            add(new TypeElementProperty("Web URL", ContentType.String));
            add(new TypeElementProperty("Site ID", ContentType.String));
        }};
    }
}
