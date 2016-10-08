package com.github.clboettcher.bonappetit.app.menu;


import android.util.Log;
import com.github.clboettcher.bonappetit.app.db.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.menu.entity.OptionEntity;
import com.github.clboettcher.bonappetit.app.menu.entity.OptionEntityType;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Data access facade for {@link OptionEntity}.
 */
public class OptionDao {

    private static final String TAG = OptionDao.class.getName();
    private BonAppetitDbHelper bonAppetitDbHelper;
    private RuntimeExceptionDao<OptionEntity, Long> dao;
    private RadioItemDao radioItemDao;

    @Inject
    public OptionDao(BonAppetitDbHelper bonAppetitDbHelper, RadioItemDao radioItemDao) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
        this.radioItemDao = radioItemDao;
        this.dao = bonAppetitDbHelper
                .getRuntimeExceptionDao(OptionEntity.class);
    }

    public void save(Collection<OptionEntity> options) {
        Preconditions.checkNotNull(options, "options");

        for (OptionEntity option : options) {
            Log.i(TAG, String.format("Saving option %s to database", option));
            if (option.getType() == OptionEntityType.RADIO) {
                radioItemDao.save(option.getRadioItemEntities());
            }
            dao.create(option);
        }
    }

    public Collection<OptionEntity> list() {
        return dao.queryForAll();
    }

}
