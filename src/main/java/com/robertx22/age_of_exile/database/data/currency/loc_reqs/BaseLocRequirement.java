package com.robertx22.age_of_exile.database.data.currency.loc_reqs;

import net.minecraft.network.chat.MutableComponent;

public abstract class BaseLocRequirement {

    public abstract MutableComponent getText();

    public abstract boolean isAllowed(LocReqContext context);

    public boolean isNotAllowed(LocReqContext context) {

        return !this.isAllowed(context);
    }

}
