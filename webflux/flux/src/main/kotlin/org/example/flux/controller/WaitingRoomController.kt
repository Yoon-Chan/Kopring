package org.example.flux.controller

import org.example.flux.service.UserQueueService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ServerWebExchange

@Controller
class WaitingRoomController(
    private val userQueueService: UserQueueService
) {

    @GetMapping("/waiting-room")
    suspend fun waitingRoom(
        @RequestParam("queue", defaultValue = "default") queue: String,
        @RequestParam("user_id") userId: Long,
        @RequestParam("redirect_url") redirectUrl: String,
        model: Model,
        serverWebExchange: ServerWebExchange
    ): String {
        val key = "user-queue-$queue-token"

        val token = serverWebExchange.request.cookies.getFirst(key)?.value ?: ""
        if(userQueueService.isAllowedByToken(queue, userId, token)) {
            return "redirect:$redirectUrl"
        }

        val rank = try {
            userQueueService.registerWaitQueue(queue, userId)
        } catch (e: Exception) {
            userQueueService.getRank(queue, userId)
        }
        model.addAttribute("number", rank)
        model.addAttribute("userId", userId)
        return "waiting-room"
    }
}