f  = namespace('lib/form')

f.section(title: 'Global Chat Notifier Settings') {
    f.entry(title: "Token", help: "${rootURL}/plugin/chat/help-globalConfig-token.html") {
        f.password(name: "hipChatToken", value: "${descriptor.getToken()}")
    }
    f.entry(title: "Room id", help: "${rootURL}/plugin/chat/help-globalConfig-room.html") {
        f.textbox(name: "hipChatRoom", value: "${descriptor.getRoom()}")
    }
    f.entry(title: "Text Color", help: "${rootURL}/plugin/chat/help-globalConfig-color.html") {
        f.textbox(name: "hipChatColor", value: "${descriptor.getColor()}")
    }
}

