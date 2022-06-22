package com.kyumgnan.shop.service;

import com.kyumgnan.shop.dto.CartDetailDto;
import com.kyumgnan.shop.dto.CartItemDto;
import com.kyumgnan.shop.dto.CartOrderDto;
import com.kyumgnan.shop.dto.OrderDto;
import com.kyumgnan.shop.entity.Cart;
import com.kyumgnan.shop.entity.CartItem;
import com.kyumgnan.shop.entity.Item;
import com.kyumgnan.shop.entity.Member;
import com.kyumgnan.shop.repository.CartItemRepository;
import com.kyumgnan.shop.repository.CartRepository;
import com.kyumgnan.shop.repository.ItemRepository;
import com.kyumgnan.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String email){

        Item item = itemRepository.findById(cartItemDto.getItemId()) //장바구니에 담을 상품 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email); //현재 로그인한 회원 email 조회 이부분을 name이나 id로 변경하면 name or id 조회가 됩니다.

        Cart cart = cartRepository.findByMemberId(member.getId()); //현재 로그인한 회원의 장바구니를 조회합니다.
        if(cart == null){ //만약 장바구니에 상품이 하나도 없을 경우 장바구니 엔티티를 생성합니다.
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId()); //현재 상품이 장바구니에 들어가있는지 조회합니다.(중복조회)

        if(savedCartItem != null){ //만약 중복된 상품일경우 기존 수량에 현재 장바구니에 담을 수량만큼 더해줍니다.
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem); //장바구니에 들어갈 상품을 저장합니다.
            return cartItem.getId();
        }
    }

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null){
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        Member curMember = memberRepository.findByEmail(email);//현재 로그인한 회원 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember(); //장바구니 상품을 저장한회원 조회


        return true;
    }

    public void updateCartItemCount(Long cartItemId, int count){ //장바구니 상품 수량 업데이트 메소드
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();

        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository
                            .findById(cartOrderDto.getCartItemId())
                            .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository
                            .findById(cartOrderDto.getCartItemId())
                            .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }

}