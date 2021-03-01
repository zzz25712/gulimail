package com.itdemo.gulimail.cart.controller;

import com.itdemo.gulimail.cart.config.CartIntercepter;
import com.itdemo.gulimail.cart.service.CartService;
import com.itdemo.gulimail.cart.vo.Cart;
import com.itdemo.gulimail.cart.vo.CartItem;
import com.itdemo.gulimail.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * 浏览器有一个name为 user-key的cookie 唯一标识 一个月后过期
 * 如果第一次使用购物车 会自动给一个临时用户身份
 * 浏览器保存这个cookie 以后每次访问都会带上这个cookie
 *
 * 登陆的话 session中保存着登陆信息
 * 没登陆的话 按照cookie里带的user-key来做
 * 第一次使用 没有临时用户的话 会自动创建一个新的
 * */
@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/getCheckedItem")
    @ResponseBody
    public List<CartItem> getCheckedItem(){
       return cartService.getCheckedItem();
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId")Long skuId,
                            @RequestParam("num")Integer num){
        cartService.countItem(skuId,num);
        return "redirect:http://cart.gulimail.com/cart.html";
    }

    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId")Long skuId,
                            @RequestParam("check")Integer check){
        cartService.checkItem(skuId,check);
        return "redirect:http://cart.gulimail.com/cart.html";
    }

    @GetMapping("/cart.html")
    public String toCartListPage(Model model) throws ExecutionException, InterruptedException {
        Cart cart = cartService.getCart();
        model.addAttribute("cart",cart);
        System.out.println(cart.getItems());
        return "cartlist";
    }

    @GetMapping("/addToCart")
    public String addTocart(@RequestParam("skuId")Long skuId,
                            @RequestParam("num")Integer num,
                            RedirectAttributes re) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId,num);
        re.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimail.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String Tosuccess(@RequestParam("skuId")Long skuId,
                            Model model){
        CartItem cartItem = cartService.findCartItem(skuId);
        model.addAttribute("item",cartItem);
        return "success";
    }

}
