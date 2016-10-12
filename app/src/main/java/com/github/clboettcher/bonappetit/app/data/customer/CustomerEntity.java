package com.github.clboettcher.bonappetit.app.data.customer;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@DatabaseTable(tableName = "CUSTOMER")
public class CustomerEntity {

    @DatabaseField(columnName = "CUSTOMER_ID", id = true)
    private Long id;

    @DatabaseField(columnName = "VALUE")
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("value", value)
                .toString();
    }
}
