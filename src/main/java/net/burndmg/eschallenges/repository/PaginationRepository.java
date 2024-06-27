package net.burndmg.eschallenges.repository;

import net.burndmg.eschallenges.data.dto.Page;
import net.burndmg.eschallenges.data.dto.PageSettings;
import net.burndmg.eschallenges.data.model.TimestampBasedSortable;

public interface PaginationRepository <T extends TimestampBasedSortable> {

    Page<T> findAllAfter(PageSettings pageSettings, Class<T> type);
}
