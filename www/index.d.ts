export type  Since = Date | number;

export type FolderType = 'inbox' | 'failed' | 'queued' | 'sent' | 'draft' | 'outbox' | 'undelivered' | 'conversations';

/**
 * @Source : https://stackoverflow.com/questions/8447735/android-sms-type-constants
 */
export enum SMSType {
    ALL = 0,
    INBOX = 1,
    SENT = 2,
    DRAFT = 3,
    OUTBOX = 4,
    FAILED = 5, // for failed outgoing messages
    QUEUED = 6// for messages to send later
}

export interface SMS {
    id: number;
    address: string;
    body: string;
    read: boolean;
    date: number;
    type: SMSType;
}

export interface SMSInbox extends SMS {
    type: SMSType.INBOX;
}

export namespace SMSReader {
    /**
     * @deprecated
     */
    export function getAllSMS(since: Since): Promise<SMSInbox[]>;

    export function filterSenders(sendersId: string[], since: Since): Promise<SMSInbox[]>;

    export function filterBody(search: string, since: Since): Promise<SMSInbox[]>;

    export function filterBodyOrSenders(search: string, sendersId: string[], since: Since): Promise<SMSInbox[]>

    export function getSmsFromFolder(folderType: FolderType, since: Since): Promise<SMS[]>;

    export function getSmsAll(since: Since): Promise<SMS[]>;

    export function getSmsInbox(since: Since): Promise<SMS[]>;
}
