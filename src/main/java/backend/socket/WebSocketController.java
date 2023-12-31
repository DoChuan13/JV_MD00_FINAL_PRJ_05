package backend.socket;

import backend.socket.dto.MessageDTO;
import backend.socket.dto.WebsocketDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin(origins = {"*"})
public class WebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @MessageMapping("/hello")
    @SendTo("/topic/chat")
    public WebsocketDTO greeting(MessageDTO message) throws Exception {
        Thread.sleep(1000); // simulated delay
        logger.info("Response===>" + message.getChat().getId());
        WebsocketDTO websocketDTO = new WebsocketDTO();
        websocketDTO.setId(message.getChat().getId());
        websocketDTO.setSentUser(message.getChatResponse().getSentUser());
        websocketDTO.setRespUser(message.getChatResponse().getRespUser());
        return websocketDTO;
        /*return new WebsocketDTO(HtmlUtils.htmlEscape(String.valueOf(message.getChat().getId())));*/
    }
}
