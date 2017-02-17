package com.manywho.services.sharepoint.types;

import com.manywho.sdk.entities.draw.elements.type.*;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.types.AbstractType;

public class Site extends AbstractType{
    public final static String NAME = "Site";

    @Override
    public TypeElementBindingCollection getBindings() {
        return new TypeElementBindingCollection() {{
            add(new TypeElementBinding(NAME, "Details about a file", NAME, new TypeElementPropertyBindingCollection() {{
                add(new TypeElementPropertyBinding("ID", "ID"));
                add(new TypeElementPropertyBinding("Created Date Time", "Created Date Time"));
                add(new TypeElementPropertyBinding("Description", "Description"));
                add(new TypeElementPropertyBinding("Last ModifiedDateTime", "Description"));

            }}));
        }};
    }

    @Override
    public String getDeveloperName() {
        return Site.NAME;
    }

    @Override
    public TypeElementPropertyCollection getProperties() {
        return new TypeElementPropertyCollection() {{
            add(new TypeElementProperty("ID", ContentType.String));
        }};
    }
}
