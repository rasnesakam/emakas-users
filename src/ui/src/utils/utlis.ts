export type DeepPartial<T> = {
    [P in keyof T]?: DeepPartial<T[P]>
}

export function copyToClipboard(text: string): Promise<void>{
    if (navigator.clipboard && window.isSecureContext) {
        return navigator.clipboard.writeText(text);
    } else {
        // Fallback for older browsers
        const textarea = document.createElement('textarea');
        textarea.value = text;
        textarea.style.position = 'absolute';
        textarea.style.left = '-9999px';
        textarea.style.top = '0';
        document.body.appendChild(textarea);
        textarea.select();
        try {
            document.execCommand('copy');
        } catch (err) {
            console.error('Copy failed:', err);
        } finally {
            document.body.removeChild(textarea);
        }
        return Promise.resolve();
    }
}

export function getCookie(name: string) {
  return document.cookie
    .split("; ")
    .find(row => row.startsWith(name + "="))
    ?.split("=")[1];
}

export const requiredScopes = [
    "openid",
    "email",
    "profile",
    "read_write:iam.emakas.net/members",
    "read_write:iam.emakas.net/teams",
    "read_write:iam.emakas.net/permissions",
    "read_write:iam.emakas.net/teamMembers",
    "read_write:iam.emakas.net/resources",
    "read_write:iam.emakas.net/applications"
]