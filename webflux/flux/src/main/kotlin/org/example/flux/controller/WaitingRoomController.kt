package org.example.flux.controller

import org.example.flux.service.UserQueueService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class WaitingRoomController(
    private val userQueueService: UserQueueService
) {

    @GetMapping("/waiting-room")
    suspend fun waitingRoom(
        @RequestParam("queue", defaultValue = "default") queue: String,
        @RequestParam("user_id") userId: Long,
        @RequestParam("redirect_url") redirectUrl: String,
        model: Model
    ): String {
        if(userQueueService.isAllowed(queue, userId)) {
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