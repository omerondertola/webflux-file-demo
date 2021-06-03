package tr.com.tolaas.spring.webflux.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

import static org.springframework.core.io.buffer.DataBufferUtils.*;

@Service
public class StdoutStreamingService {

    //@Autowired
    //DefaultDataBufferFactory factory;

    public Flux<String> getStdoutStream() throws IOException {
        DefaultDataBufferFactory defaultDataBufferFactory = new DefaultDataBufferFactory();
        Flux<DataBuffer> dataBufferFlux = read(new FileSystemResource(new File("./deneme.txt")), 0, defaultDataBufferFactory, 128);
        return dataBufferFlux.map(dataBuffer -> {
            return new Date(System.currentTimeMillis())+ " "+dataBuffer.toString();
        });
    }

    /**
     * Read the given {@code Resource} into a {@code Flux} of {@code DataBuffer}s
     * starting at the given position.
     * <p>If the resource is a file, it is read into an
     * {@code AsynchronousFileChannel} and turned to {@code Flux} via
     * {@link #readAsynchronousFileChannel(Callable, DataBufferFactory, int)} or else
     * fall back on {@link #readByteChannel(Callable, DataBufferFactory, int)}.
     * Closes the channel when the flux is terminated.
     * @param resource the resource to read from
     * @param position the position to start reading from
     * @param dataBufferFactory the factory to create data buffers with
     * @param bufferSize the maximum size of the data buffers
     * @return a flux of data buffers read from the given channel
     */
    public Flux<DataBuffer> read(
            Resource resource,
            long position,
            DataBufferFactory dataBufferFactory,
            int bufferSize) {
        try {
            if (resource.isFile()) {
                File file = resource.getFile();
                return readAsynchronousFileChannel(
                        () -> AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.READ),
                        position, dataBufferFactory, bufferSize);
            }
        }
        catch (IOException ignore) {
            // fallback to resource.readableChannel(), below
        }
        Flux<DataBuffer> result = readByteChannel(resource::readableChannel, dataBufferFactory, bufferSize);
        return position == 0 ? result : skipUntilByteCount(result, position);
    }
}
