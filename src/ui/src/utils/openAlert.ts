export function openAlert(message: string): Promise<void> {
    return new Promise((resolve) => {
        alert(message);
        resolve();
    });
}