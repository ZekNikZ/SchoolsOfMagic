package dev.mattrm.schoolsofmagic.client.data;

import dev.mattrm.schoolsofmagic.common.data.JsonData;
import dev.mattrm.schoolsofmagic.common.data.JsonDataType;

public abstract class ClientDataManager<D extends JsonData<T>, T extends JsonDataType<D>, O> {
    public abstract void loadData(O data);
}
