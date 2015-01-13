package chequebook;

import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

/**
 * Created by rurik
 */
public abstract class CellConverter<T> implements Converter<String, T> {
    private Class<T> modelType;

    public CellConverter(Class<T> modelType) {
        this.modelType = modelType;
    }

    @Override
    public T convertToModel(String value, Class<? extends T> targetType, Locale locale) throws ConversionException {
        throw new IllegalStateException();
    }

    @Override
    public String convertToPresentation(T value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return value == null ? null : convert(value);
    }

    protected abstract String convert(T t);

    @Override
    public Class<T> getModelType() {
        return modelType;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
