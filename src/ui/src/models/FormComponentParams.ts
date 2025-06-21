export interface FormComponentParams<T> {
    initialData?: T;
    beforeSubmit?: (entity: T) => void;
    afterSubmit?: (entity: T) => void;
}