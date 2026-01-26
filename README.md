<div>
<img src="screenshots/4.jpg" alt="drawing" style="width:auto;"/>

</div>

# Scanner ML Kit

Este é um projeto de exemplo para Android que demonstra o uso do [Document Scanner do Google ML Kit](https://developers.google.com/ml-kit/vision/document-scanner) para digitalizar e gerenciar documentos.

## Descrição

O aplicativo permite ao usuário digitalizar documentos usando a câmera do dispositivo ou importando imagens da galeria. Cada digitalização é salva como um novo arquivo PDF com um nome exclusivo.

A tela principal exibe uma lista de todos os documentos PDF que foram gerados, mostrando uma prévia da primeira página de cada um. Para cada documento na lista, o usuário tem as opções de:

*   **Visualizar**: Abrir o PDF em um visualizador de PDF externo.
*   **Exportar**: Compartilhar ou salvar o arquivo PDF em outro local.

## Preview

<div>
<img src="screenshots/1.jpg" alt="drawing" style="width:300px;"/>
<img src="screenshots/2.jpg" alt="drawing" style="width:300px;"/>
</div>

## Tecnologias e Dependências

Este projeto utiliza as seguintes tecnologias e bibliotecas:

*   **[Kotlin](https://kotlinlang.org/)**: Linguagem de programação principal.
*   **[Jetpack Compose](https://developer.android.com/jetpack/compose)**: Toolkit de UI moderno para construir interfaces de usuário nativas.
*   **[Google ML Kit Document Scanner](https://developers.google.com/ml-kit/vision/document-scanner)**: Para fornecer a funcionalidade de digitalização de documentos.
    *   `com.google.android.gms:play-services-mlkit-document-scanner:16.0.0`
*   **[Coil 3](https://coil-kt.github.io/coil/)**: Uma biblioteca de carregamento de imagem para Kotlin.
    *   `io.coil-kt.coil3:coil-compose:3.3.0`
*   **Fonte Inter**: A tipografia do aplicativo utiliza a família de fontes Inter para um visual limpo e moderno.
*   **[AndroidX Libraries](https://developer.android.com/jetpack/androidx)**:
    *   `androidx.core:core-ktx`
    *   `androidx.lifecycle:lifecycle-runtime-ktx`
    *   `androidx.activity:activity-compose`

## Uso

1.  Clone o repositório.
2.  Abra o projeto no Android Studio.
3.  Execute o aplicativo em um emulador ou dispositivo Android.
4.  Clique no botão "Scan PDF" para iniciar o fluxo de digitalização.
5.  Após a conclusão da digitalização, o novo documento aparecerá na lista na tela principal.
6.  Para cada documento na lista, você pode clicar em "View" para abri-lo ou "Export" para compartilhá-lo.

## Licença

```
Copyright 2024 Davi Gomes Florencio

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
