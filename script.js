const copyButton = document.getElementById('copyCommandBtn');

if (copyButton) {
    copyButton.addEventListener('click', async () => {
        const command = 'cd frontend && npm install && npm run dev';

        try {
            await navigator.clipboard.writeText(command);
            copyButton.textContent = '복사 완료';
        } catch (error) {
            console.error(error);
            copyButton.textContent = '복사 실패';
        }

        window.setTimeout(() => {
            copyButton.textContent = '프런트 실행 명령 복사';
        }, 2200);
    });
}
