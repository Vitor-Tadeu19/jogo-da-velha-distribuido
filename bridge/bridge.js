const WebSocket = require('ws');
const net = require('net');

const WS_PORT = 8080;
const JAVA_PORT = 5000;

const wss = new WebSocket.Server({ port: WS_PORT });

console.log(`Bridge WebSocket iniciada na porta ${WS_PORT}`);

wss.on('connection', (ws, req) => {
    const clientAddr = req.socket.remoteAddress;
    console.log(`[WS] Novo cliente conectado: ${clientAddr}`);

    let tcp = null;
    let buffer = '';
    let connected = false;

    ws.on('message', (msg) => {
        const text = msg.toString().trim();

        if (!connected && text.startsWith('CONNECT ')) {
            const javaHost = text.slice(8).trim();
            console.log(`[TCP] Conectando ao Java em ${javaHost}:${JAVA_PORT}`);

            tcp = new net.Socket();

            tcp.connect(JAVA_PORT, javaHost, () => {
                connected = true;
                console.log(`[TCP] Conexão com Java estabelecida em ${javaHost}`);
            });

            tcp.on('data', (data) => {
                buffer += data.toString();
                const lines = buffer.split('\n');
                buffer = lines.pop();

                lines.forEach((line) => {
                    const trimmed = line.trim();
                    if (trimmed && ws.readyState === WebSocket.OPEN) {
                        console.log(`[Java → Browser] ${trimmed}`);
                        ws.send(trimmed);
                    }
                });
            });

            tcp.on('error', (err) => {
                console.error(`[TCP] Erro: ${err.message}`);
                if (ws.readyState === WebSocket.OPEN) {
                    ws.send('ERRO Não foi possível conectar ao servidor Java em ' + javaHost);
                    ws.close();
                }
            });

            tcp.on('close', () => {
                console.log(`[TCP] Conexão com Java encerrada`);
                if (ws.readyState === WebSocket.OPEN) {
                    ws.close();
                }
            });

            return;
        }

        if (tcp && tcp.writable) {
            console.log(`[Browser → Java] ${text}`);
            tcp.write(text + '\n');
        }
    });

    ws.on('close', () => {
        console.log(`[WS] Cliente desconectado: ${clientAddr}`);
        if (tcp) tcp.destroy();
    });

    ws.on('error', (err) => {
        console.error(`[WS] Erro: ${err.message}`);
        if (tcp) tcp.destroy();
    });
});
