# Pular Anúncios

![Pular Anúncios Imagem](./images/logo_web_hi_res_512.png)

Aplicativo de código aberto para Android que pula e silencia anúncios de vídeo do YouTube. Nunca mais precise clicar no botão "Pular Anúncio" para pular anúncios irritantes! Este aplicativo faz isso para você :)

Pular Anúncios faz três coisas quando você abre o aplicativo:
- Fica aguardando anúncios de vídeo aparecerem;
- Quando um anúncio de vídeo aparece, ele silencia o áudio;
- Clica no botão <kbd>Pular Anúncio</kbd> assim que ele fica habilitado, e restaura o volume do seu vídeo.

O aplicativo Pular Anúncios atua como um Serviço de Acessibilidade para poder clicar no botão automaticamente, então após instalá-lo, você precisará abrir o aplicativo e ativar manualmente o serviço. Este é um protocolo de segurança do Android para ativar Serviços de Acessibilidade, já que tais serviços podem interagir com a sua tela.

Não se preocupe, Pular Anúncios interage SOMENTE com a tela do YouTube. Fique à vontade para verificar o código-fonte :)

Este aplicativo não é filiado ao YouTube ou Google.

## Usando pela primeira vez

Este aplicativo usa a API de [Serviço de Acessibilidade](https://developer.android.com/guide/topics/ui/accessibility/service), então é necessário uma configuração inicial para que ele funcione adequadamente.

Siga os passos abaixo para ativar o aplicativo:

- [Install apk](https://github.com/alfeugds/adskipper/releases/latest);
- Abra o aplicativo e ative a opção "Habilitar Pular Anúncios";
- Isso te levará até `Settings -> Accessibility`. Clique em **Pular Anúncios** e ative o serviço;
- O sistema te avisará que o aplicativo terá acesso para ver e interagir com todas as telas. Confirme a opção para ativar o serviço;
- Aproveite :)

## Building it yourself

This is a standard Android Studio Project, so you can import this project in Android Studio and just build it.
The .apk will be at app\build\outputs\apk\debug\app-debug.apk
