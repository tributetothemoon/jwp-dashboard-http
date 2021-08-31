package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.web.ContentType;
import nextstep.jwp.web.HttpRequest;
import nextstep.jwp.web.HttpResponse;
import nextstep.jwp.web.HttpStatus;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoginController extends AbstractController {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoginProcess(request)) {
            loginProcess(request, response);
            return;
        }
        loginPageProcess(response);
    }

    private boolean isLoginProcess(HttpRequest request) {
        return request.getParameter("account") != null && request.getParameter("password") != null;
    }

    public void loginProcess(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(
                        foundUser -> passwordCheckProcess(foundUser, password, response),
                        () -> responseUnauthorized(response));
    }

    private void passwordCheckProcess(User foundUser, String password, HttpResponse response) {
        if (foundUser.checkPassword(password)) {
            response.status(HttpStatus.FOUND)
                    .location("/index.html");
            return;
        }
        responseUnauthorized(response);
    }

    private void responseUnauthorized(HttpResponse response) {
        response.status(HttpStatus.FOUND)
                .location("/401.html");
    }

    private void loginPageProcess(HttpResponse response) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String loginHtmlPath = resource.getPath();

        response.status(HttpStatus.OK)
                .contentType(ContentType.toHttpNotationFromFileExtension(resource.getFile()))
                .body(new String(Files.readAllBytes(Path.of(loginHtmlPath))));
    }
}
