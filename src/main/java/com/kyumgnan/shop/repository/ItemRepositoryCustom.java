package com.kyumgnan.shop.repository;

import com.kyumgnan.shop.dto.ItemSearchDto;
import com.kyumgnan.shop.dto.MainItemDto;
import com.kyumgnan.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

}