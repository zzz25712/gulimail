package com.itdemo.gulimail.order.web;

import com.itdemo.common.exception.NoStockException;
import com.itdemo.gulimail.order.service.OrderService;
import com.itdemo.gulimail.order.vo.OrderConfirmVo;
import com.itdemo.gulimail.order.vo.OrderResponVo;
import com.itdemo.gulimail.order.vo.OrderSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo itemVo = orderService.getOrderItemVo();
        model.addAttribute("item",itemVo);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo,Model model) throws NoStockException {
        OrderResponVo responVo = orderService.submitOrder(vo);
        if(responVo.getCode() == 0){
            model.addAttribute("responVo",responVo);
            return "pay";
        }else {
            return "redirect:http://order.gulimail.com/toTrade";
        }

    }
}
